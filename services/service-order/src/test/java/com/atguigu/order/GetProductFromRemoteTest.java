package com.atguigu.order;

import com.atguigu.order.service.OrderService;
import com.atguigu.order.service.impl.OrderServiceImpl;
import com.atguigu.product.bean.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class GetProductFromRemoteTest {
    @Autowired
    OrderServiceImpl orderService;

    @Test
    public void testGetProductFromRemote(){
        Product product = orderService.getProductFromRemote(10L);
        System.out.println(product);
    }
}
