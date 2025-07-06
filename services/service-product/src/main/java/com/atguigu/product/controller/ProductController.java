package com.atguigu.product.controller;

import com.atguigu.product.bean.Product;
import com.atguigu.product.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@RestController
//@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    ProductService productService;
    //查询商品

    @GetMapping("/product/{id}")
    public Product getProduct(@PathVariable("id") Long productId,
                              HttpServletRequest request){
        System.out.println("hello");
        String header = request.getHeader("X-Token");
        System.out.println("Header-X-Token = " + header);
        Product product = productService.getProduct(productId);

//        int i = 10/0;
       /* try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }*/
        return  product;
    }
}
