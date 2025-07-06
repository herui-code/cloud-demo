package com.atguigu.order.service;

import com.atguigu.order.bean.Order;
import org.springframework.stereotype.Service;

@Service
public interface OrderService {

    public Order createOrder(Long productId, Long userId);
}
