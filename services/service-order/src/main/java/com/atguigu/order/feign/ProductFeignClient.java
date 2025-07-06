package com.atguigu.order.feign;

import com.atguigu.order.feign.fallback.ProductFeignClientFallback;
import com.atguigu.product.bean.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

//违反sentinel规则，拦截策略；SentinelFeignAutoConfiguration
//
//@RequestMapping("/api/product")
@FeignClient(value = "service-product",fallback = ProductFeignClientFallback.class )//feign客户端
//http://service-product/product/{id}
public interface ProductFeignClient {

    @GetMapping("/product/{id}")
    Product getProductById(@PathVariable("id") Long id);
}


