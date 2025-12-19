package com.server.dto;

import com.server.entity.OrderItem;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderDTO {
    // 桌号
    private Integer tableId;

    // 用户ID (是谁下的单)
    private Long userId;

    // 总金额 (报错 Cannot resolve method 'getTotalAmount' 就是因为缺这个)
    private BigDecimal totalAmount;

    // 订单包含的菜品列表
    private List<OrderItem> items;
}
