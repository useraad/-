package com.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("product_review")
public class ProductReview {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long productId;
    private String username;
    private Integer rating;
    private String comment;
    private LocalDateTime createTime;
}