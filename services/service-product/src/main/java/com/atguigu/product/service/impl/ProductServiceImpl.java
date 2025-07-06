package com.atguigu.product.service.impl;

import com.atguigu.product.bean.Product;
import com.atguigu.product.service.ProductService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

@Service
public class ProductServiceImpl implements ProductService {
    @Override
    public Product getProduct(Long productId) {
        Product product = new Product();
        product.setId(productId);
        product.setPrice(new BigDecimal(99));
        product.setName("苹果"+ productId);
        product.setNum(2);
        /*try {
            TimeUnit.SECONDS.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }*/
        return product;
    }
}
