package com.server.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.server.comon.Result;
import com.server.dto.OrderDTO;
import com.server.entity.OrderItem;
import com.server.entity.Orders;
import com.server.entity.Product;
import com.server.mapper.OrderItemMapper;
import com.server.mapper.OrdersMapper;
import com.server.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class OrderService extends ServiceImpl<OrdersMapper, Orders> {

    @Autowired private ProductMapper productMapper;
    @Autowired private OrdersMapper ordersMapper;
    @Autowired private OrderItemMapper orderItemMapper;

    @Transactional(rollbackFor = Exception.class)
    public Result<String> createOrder(OrderDTO dto) {
        BigDecimal totalAmount = BigDecimal.ZERO;

        // 1. 生成主订单
        Orders order = new Orders();

        // 生成订单号 (时间戳+随机数)
        String timeId = new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());
        int random = (int) ((Math.random() * 9 + 1) * 100);
        order.setOrderNo(timeId + random);

        order.setTableId(dto.getTableId());

        // 🔥 关键修复：必须记录是谁开的台，否则“我的桌子”功能会失效！
        order.setUserId(dto.getUserId());

        order.setStatus(0); // 默认为0=待支付 (支付成功后才变1)
        order.setCreateTime(LocalDateTime.now());

        // 2. 第一次循环：校验库存 + 计算总价
        // ❌ 错误写法: for (OrderDTO.Item itemDto : dto.getItems())
        // ✅ 正确写法: 👇
        for (OrderItem itemDto : dto.getItems()) {
            // 前端传过来的 itemDto.getId() 是商品ID
            Product product = productMapper.selectById(itemDto.getId());

            if (product == null) {
                throw new RuntimeException("商品不存在");
            }
            if (product.getStock() < itemDto.getQuantity()) {
                throw new RuntimeException("商品 " + product.getName() + " 库存不足");
            }

            // 扣库存
            product.setStock(product.getStock() - itemDto.getQuantity());
            productMapper.updateById(product);

            // 累加金额
            totalAmount = totalAmount.add(product.getPrice().multiply(BigDecimal.valueOf(itemDto.getQuantity())));
        }

        order.setTotalAmount(totalAmount);
        ordersMapper.insert(order);

        // 3. 第二次循环：保存订单详情
        for (OrderItem itemDto : dto.getItems()) {
            Product product = productMapper.selectById(itemDto.getId());

            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setProductId(product.getId()); // 记录真实的商品ID
            orderItem.setProductName(product.getName());
            orderItem.setPrice(product.getPrice());
            orderItem.setQuantity(itemDto.getQuantity());

            // 注意：这里不要 setCreateTime，让数据库自动生成，或者在这设为 now
            // 这样加餐的时候时间就是最新的
            orderItem.setCreateTime(LocalDateTime.now());

            orderItemMapper.insert(orderItem);
        }

        return Result.success(order.getOrderNo());
    }
}