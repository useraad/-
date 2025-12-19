package com.server.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.server.entity.OrderItem;
import com.server.mapper.OrderItemMapper;
import org.springframework.stereotype.Service;

@Service
public class OrderItemService extends ServiceImpl<OrderItemMapper, OrderItem> {
}