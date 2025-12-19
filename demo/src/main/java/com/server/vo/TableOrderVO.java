package com.server.vo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class TableOrderVO {
    private String productName;
    private BigDecimal price;
    private int quantity;          // 总份数
    private List<String> orderNos; // 所有订单号（去重）

    public TableOrderVO() {
        this.quantity = 0;
        this.orderNos = new ArrayList<>();
    }
    public void addQuantity(int q) { this.quantity += q; }
    public void addOrderNo(String no) { if (!orderNos.contains(no)) orderNos.add(no); }
}