package com.atguigu.order.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.atguigu.order.bean.Order;
import com.atguigu.order.properties.OrderProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;
import com.atguigu.order.service.OrderService;

@CrossOrigin
//@RequestMapping("/api/order")
@Slf4j
@RefreshScope//配置文件自动刷新。修改注册中心配置文件后，自动获取最新值
@RestController
public class OrderController {

    @Autowired
    OrderService orderService;

    @Autowired
    OrderProperties orderProperties;

    @GetMapping("/config")
    public String config(){
        return "order.timeout:"+orderProperties.getTimeout()
                +",order.auto-confirm:"+orderProperties.getAutoConfirm()+",order.dbUrl:"+orderProperties.getDbUrl();
    }

    //创建订单
    @RequestMapping("/create")
    public Order createOrder(@RequestParam("productId") Long productId,
                             @RequestParam("userId") Long userId){
        Order order = orderService.createOrder(productId, userId);
        return order;
    }
    //官方文档显示，热点参数限流对dubbo是支持的，对于web接口，是不支持的。如果需要在web接口中使用热点规则
    //需要自定义埋点方式，也就是自定义一个方法，然后使用@SentinelResource注解。
    //注意,自定义埋点的资源名不要和适配器生成的资源名重名，否则会导则重复统计。
    @RequestMapping("/seckill")
    @SentinelResource(value = "seckill-order",fallback = "seckillFallback")
    public Order seckill(@RequestParam(value = "productId",defaultValue = "1000") Long productId,
                             @RequestParam(value = "userId",required = false) Long userId){
        Order order = orderService.createOrder(productId, userId);
        order.setId(Long.MAX_VALUE);
        return order;
    }
    //@SentinelResource中使用fallback和blockHandler的区别
    //1.fallback和blockHandler的区别
    //fallback：当资源被限流或者降级后，会调用对应的方法处理，返回fallback对应的结果。
    //blockHandler：当资源被限流或者降级后，会调用对应方法处理，返回blockHandler对应结果。

    //1.有blockHandler，fallback方法会失效。没有blockHandler，fallback方法会生效。
    //2.fallBack比blockHandler的好处为，可以处理业务异常，也就是所有的异常Throwable.
    //3.blockHandler只能处理BlockException，不能处理业务异常。
    @SentinelResource(value = "seckill-order",fallback = "seckillFallback")
    public Order seckillFallback(Long productId,
                                 Long userId,
                                 Throwable e){
        System.out.println("seckillFallback...");
        Order order = new Order();
        order.setId(productId);
        order.setUserId(userId);
        order.setAddress("异常信息："+e.getClass());

        return order;
    }
    //writeDb和readDb方法为测试 sentinel关联规则
    //假设writeDb和readDb访问同一个资源
    //要求：当writeDb的请求访问数量大了后，才限制readDb的访问.
    //     当writeDb的请求访问数量不大时候，随便访问readDb
    @GetMapping("/writeDb")
    public String writeDb(){
        return "writeDb success ... ";
    }

    @GetMapping("/readDb")
    public String readDb(){
        log.info("readDb success ....");
        return "readDb success ... ";
    }
}
