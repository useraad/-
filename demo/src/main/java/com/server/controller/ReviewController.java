package com.server.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.server.comon.Result;
import com.server.entity.ProductReview;
import com.server.mapper.ProductReviewMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/review")
public class ReviewController {

    @Autowired
    private ProductReviewMapper reviewMapper;

    // 获取某个菜品的评价列表
    @GetMapping("/list/{productId}")
    public Result<List<ProductReview>> list(@PathVariable Long productId) {
        return Result.success(reviewMapper.selectList(
                new LambdaQueryWrapper<ProductReview>()
                        .eq(ProductReview::getProductId, productId)
                        .orderByDesc(ProductReview::getCreateTime)
        ));
    }

    // 提交评价
    @PostMapping("/add")
    public Result<Boolean> add(@RequestBody ProductReview review) {
        review.setCreateTime(LocalDateTime.now());
        reviewMapper.insert(review);
        return Result.success(true);
    }
}