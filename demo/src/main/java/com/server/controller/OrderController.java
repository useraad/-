package com.server.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.server.comon.Result;
import com.server.dto.OrderDTO;
import com.server.entity.OrderItem;
import com.server.entity.Orders;
import com.server.entity.SysUser;
import com.server.service.OrderItemService;
import com.server.service.OrderService;
import com.server.service.ProductService;
import com.server.service.SysUserService;
import com.server.vo.TableOrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderItemService orderItemService;
    @Autowired
    private  ProductService productService;

    @Autowired
    private SysUserService userService; // 注入用户服务

    // 1. 下单接口
    @PostMapping("/create")
    public Result<String> create(@RequestBody OrderDTO orderDTO) {
        return orderService.createOrder(orderDTO);
    }

    // 2. 获取所有订单列表（后厨/管理端用）
    @GetMapping("/list")
    public Result<List<Orders>> list() {
        return Result.success(orderService.list());
    }

    // 3. 修改订单状态（例如：后厨点击出餐）
    @PutMapping("/status/{id}/{status}")
    public Result<Boolean> updateStatus(@PathVariable Long id, @PathVariable Integer status) {
        Orders order = new Orders();
        order.setId(id);
        order.setStatus(status);
        return Result.success(orderService.updateById(order));
    }



    /**
     * 4. 普通支付接口 (模拟微信/支付宝)
     * 对应前端: axios.put(`/order/pay/${orderNo}`)
     */
    @PutMapping("/pay/{orderNo}")
    public Result<Boolean> pay(@PathVariable String orderNo) {
        // 查询订单
        Orders order = orderService.getOne(new LambdaQueryWrapper<Orders>().eq(Orders::getOrderNo, orderNo));
        if (order == null) {
            return Result.error("订单不存在");
        }
        // 修改状态为 1 (已支付/制作中)
        order.setStatus(1);
        orderService.updateById(order);
        return Result.success(true);
    }

    /**
     * 5. 余额支付接口 (扣除用户余额)
     * 对应前端: axios.post('/order/pay/balance', ...)
     */
    @PostMapping("/pay/balance")
    public Result<Boolean> payWithBalance(@RequestBody Map<String, Object> params) {
        String orderNo = (String) params.get("orderNo");
        // 防止前端传过来是 Integer 类型转换报错
        Long userId = Long.valueOf(params.get("userId").toString());

        // 1. 查订单
        Orders order = orderService.getOne(new LambdaQueryWrapper<Orders>().eq(Orders::getOrderNo, orderNo));
        if (order == null) return Result.error("订单不存在");
        if (order.getStatus() != 0) return Result.error("订单已支付");

        // 2. 查用户余额
        SysUser user = userService.getById(userId);
        if (user == null) return Result.error("用户不存在");

        // 3. 检查余额是否充足
        if (user.getBalance() == null || user.getBalance().compareTo(order.getTotalAmount()) < 0) {
            return Result.error("余额不足，请充值！");
        }

        // 4. 扣款 & 更新用户
        user.setBalance(user.getBalance().subtract(order.getTotalAmount()));
        userService.updateById(user);

        // 5. 更新订单状态
        order.setStatus(1); // 设为已支付
        orderService.updateById(order);

        return Result.success(true);
    }



    @GetMapping("/table/{tableId}")
    public Result<List<TableOrderVO>> getTableOrders(@PathVariable Integer tableId) {
        // 1. 查出该桌所有「未完成」订单
        List<Orders> orders = orderService.list(
                new LambdaQueryWrapper<Orders>()
                        .eq(Orders::getTableId, tableId)
                        .ne(Orders::getStatus, 2)   // 2=已完成，排除
                        .orderByDesc(Orders::getCreateTime)
        );

        // 2. 聚合：订单 → 菜品明细
        Map<Long, TableOrderVO> map = new LinkedHashMap<>();
        for (Orders o : orders) {
            List<OrderItem> items = orderItemService.list(
                    new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, o.getId())
            );
            for (OrderItem i : items) {
                Long key = i.getProductId();
                TableOrderVO vo = map.computeIfAbsent(key, k -> new TableOrderVO());
                vo.setProductName(i.getProductName());
                vo.setPrice(i.getPrice());
                vo.addQuantity(i.getQuantity());   // 累加份数
                vo.addOrderNo(o.getOrderNo());     // 记录所有订单号
            }
        }
        return Result.success(new ArrayList<>(map.values()));
    }

    @GetMapping("/table/status")
    public Result<List<Map<String, Object>>> getTableStatus() {
        List<Map<String, Object>> list = new ArrayList<>();

        // 1. 查出所有“未完成”的订单 (status != 2)
        List<Orders> activeOrders = orderService.list(
                new LambdaQueryWrapper<Orders>().ne(Orders::getStatus, 2)
        );

        // 转成 Map 方便查找: <桌号, 订单对象>
        Map<Integer, Orders> activeMap = activeOrders.stream()
                .collect(Collectors.toMap(Orders::getTableId, o -> o, (o1, o2) -> o1));

        // 2. 模拟构建 12 张桌子的状态
        for (int i = 1; i <= 12; i++) {
            Map<String, Object> status = new HashMap<>();
            status.put("tableId", i);

            if (activeMap.containsKey(i)) {
                // 有人
                status.put("status", "busy");
                status.put("orderInfo", activeMap.get(i));
            } else {
                // 没人
                status.put("status", "free");
            }
            list.add(status);
        }

        return Result.success(list);
    }
    // === 新增：根据菜名模糊搜索菜品 (供 AI 助手一键添加使用) ===@\

    @GetMapping("/product/search")
    public Result<com.server.entity.Product> searchProduct(@RequestParam String name) {
        // 这里的 productService 需要你在类顶部 @Autowired 注入一下
        // 或者直接用 productMapper 也可以
        com.server.entity.Product product = productService.getOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.server.entity.Product>()
                        .like(com.server.entity.Product::getName, name)
                        .last("limit 1")
        );
        return Result.success(product);
    }

}