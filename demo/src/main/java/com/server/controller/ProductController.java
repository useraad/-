package com.server.controller;

import com.server.comon.Result;
import com.server.entity.Product;
import com.server.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.server.entity.OrderItem;
import com.server.service.OrderItemService;
import java.util.*;
import java.util.stream.Collectors;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;
    @Autowired
    private OrderItemService orderItemService;
    @GetMapping("/list")
    public Result<List<Product>> list() {
        return Result.success(productService.list());
    }

    // 新增
    @PostMapping("/add")
    public Result<?> addProduct(@RequestBody Product product) {
        productService.save(product);
        return Result.success(null);
    }

    // 修改
    @PutMapping("/update")
    public Result<?> updateProduct(@RequestBody Product product) {
        productService.updateById(product);
        return Result.success(null);
    }
    // 删除
    @DeleteMapping("/delete/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(productService.removeById(id));
    }
    // ====================== AI 协同过滤推荐 ======================

    @GetMapping("/recommend")
    public Result<List<Product>> recommend(@RequestParam Long productId) {
        // 1. 查出“买过该商品”的所有订单明细
        List<OrderItem> items = orderItemService.list(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getProductId, productId));
        if (items.isEmpty()) return Result.success(Collections.emptyList());

        // 2. 这些订单里还买了啥（排除自己）
        List<Long> orderIds = items.stream().map(OrderItem::getOrderId).collect(Collectors.toList());
        List<Long> productIds = orderItemService.list(
                        new LambdaQueryWrapper<OrderItem>()
                                .in(OrderItem::getOrderId, orderIds)
                                .ne(OrderItem::getProductId, productId))
                .stream().map(OrderItem::getProductId).distinct().collect(Collectors.toList());
        if (productIds.isEmpty()) {
            return Result.success(Collections.emptyList());
        }
        // 3. 取前 3 个商品返回
        List<Product> list = productService.listByIds(productIds);
        return Result.success(list.size() > 3 ? list.subList(0, 3) : list);
    }
}