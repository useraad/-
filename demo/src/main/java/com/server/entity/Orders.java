package com.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("orders") // 👈 必须是 orders，对应你的数据库
public class Orders {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String orderNo;
    private Integer tableId;
    private Long userId; // 刚才 SQL 加的就是这个字段
    private BigDecimal totalAmount;
    private Integer status;
    private LocalDateTime createTime;
}