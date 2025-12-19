package com.server.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.server.entity.Product;
import com.server.mapper.ProductMapper;
import org.springframework.stereotype.Service;

/**
 * 关键点：extends ServiceImpl<Mapper, Entity>
 * 继承了这个类，你就自动拥有了 list(), save(), updateById(), removeById() 等方法
 * 不需要自己再写一遍了
 */
@Service
public class ProductService extends ServiceImpl<ProductMapper, Product> {

}