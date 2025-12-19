package com.server.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.server.comon.Result;
import com.server.entity.OrderItem;
import com.server.entity.Orders;
import com.server.service.OrderItemService;
import com.server.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/stats")
public class StatisticsController {

    @Autowired private OrderService orderService;
    @Autowired private OrderItemService orderItemService;

    @GetMapping("/dashboard")
    public Result<Map<String, Object>> dashboard() {
        Map<String, Object> data = new HashMap<>();

        // ================= 1. 今日数据 (SQL聚合优化版) =================
        // 以前是查出List再遍历，现在直接让数据库算好返回 Map

        LocalDateTime startOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

        QueryWrapper<Orders> todayQuery = new QueryWrapper<>();
        todayQuery.select("IFNULL(SUM(total_amount), 0) as totalAmount", "COUNT(*) as orderCount")
                .ge("create_time", startOfDay)
                .le("create_time", endOfDay)
                .ne("status", 0); // 排除待支付

        // listMaps 返回的是 [{totalAmount=100, orderCount=5}]
        Map<String, Object> todayResult = orderService.getMap(todayQuery);

        data.put("todayRevenue", todayResult != null ? todayResult.get("totalAmount") : 0);
        data.put("todayOrderCount", todayResult != null ? todayResult.get("orderCount") : 0);


        // ================= 2. 热销菜品 Top 5 (SQL分组排序优化版) =================
        // SQL: SELECT product_name, SUM(quantity) as total FROM order_item GROUP BY product_name ORDER BY total DESC LIMIT 5

        QueryWrapper<OrderItem> top5Query = new QueryWrapper<>();
        top5Query.select("product_name as name", "SUM(quantity) as value")
                .groupBy("product_name")
                .orderByDesc("value")
                .last("LIMIT 5"); // 只取前5

        List<Map<String, Object>> top5 = orderItemService.listMaps(top5Query);
        data.put("topProducts", top5);


        // ================= 3. 近7天销售趋势 (新增真实逻辑) =================
        // SQL: SELECT DATE(create_time) as date, SUM(total_amount) as amount ... GROUP BY date

        LocalDate sevenDaysAgo = LocalDate.now().minusDays(6); // 含今天共7天

        QueryWrapper<Orders> trendQuery = new QueryWrapper<>();
        trendQuery.select("DATE_FORMAT(create_time, '%Y-%m-%d') as date", "IFNULL(SUM(total_amount), 0) as amount")
                .ge("create_time", sevenDaysAgo.atStartOfDay())
                .ne("status", 0)
                .groupBy("date")
                .orderByAsc("date");

        List<Map<String, Object>> trendList = orderService.listMaps(trendQuery);

        // 处理日期补全（防止某天没生意，数据库没返回该日期，导致图表断裂）
        List<String> dateAxis = new ArrayList<>();
        List<BigDecimal> amountList = new ArrayList<>();

        Map<String, BigDecimal> tempMap = new HashMap<>();
        for (Map<String, Object> m : trendList) {
            tempMap.put(m.get("date").toString(), new BigDecimal(m.get("amount").toString()));
        }

        for (int i = 0; i < 7; i++) {
            String dateStr = sevenDaysAgo.plusDays(i).toString();
            dateAxis.add(dateStr); // x轴: 2023-12-18
            amountList.add(tempMap.getOrDefault(dateStr, BigDecimal.ZERO)); // y轴
        }

        data.put("trendDates", dateAxis);
        data.put("trendAmounts", amountList);

        return Result.success(data);
    }
}