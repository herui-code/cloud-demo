package com.atguigu.order.service.impl;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.atguigu.order.bean.Order;
import com.atguigu.order.feign.ProductFeignClient;
import com.atguigu.product.bean.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.stereotype.Service;
import com.atguigu.order.service.OrderService;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    DiscoveryClient discoveryClient;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    LoadBalancerClient loadBalancerClient;

    @Autowired
    ProductFeignClient productFeignClient;

    //违法规则处理策略：1.查找是否标注blockhander,标注了就执行blockhandler标注的方法;如果没有标注blockhandler,就查找
    //是否标注了fallback，标注了就执行fallback标注的方法;如果没有标注fallback，就查找
    //是否标注了defaulltfallback,标注了就执行defaulltfallback标注的方法,如果没有标注，
    //那么就将异常抛给服务器，使用web默认处理机制(500异常),或则采用全局异常处理机制拦截
    @SentinelResource(value = "createOrder",
            blockHandler = "createOrderBlockHandler")
    @Override
    public Order createOrder(Long productId, Long userId) {
        Order order = new Order();
//        Product product = getProductFromRemote(productId);
        //调用负载均衡方式
//        Product product = getProductFromRemoteWithLoadBalance(productId);

        //3.调用基于注解的负载均衡
//        Product product = getProductFromRemoteWithLoadBalanceAnnotation(productId);

        //4.通过openfein 远程调用product服务
        Product product = productFeignClient.getProductById(100L);
        order.setId(1L);
        //总金额需要远程调用product服务，然后根据商品计算

        //任何时候，对某一段代码做资源控制，SphU
        //
//        try {
//            SphU.entry("hahah");
//
//        } catch (BlockException e) {
//            //编码处理
//        }


        //TODO
        //计算总金额
        BigDecimal totalAmount = product.getPrice().multiply(new BigDecimal(product.getNum()));
        order.setTotalAmount(totalAmount);
        order.setNickName("张三");
        order.setUserId(userId);
        order.setAddress("四川");
        //TODO 远程查询商品列表

        order.setProdctList(Arrays.asList(product));
        return order;
    }

    //兜底回调
    public Order createOrderBlockHandler(Long productId, Long userId, BlockException e) {
        Order order = new Order();

        order.setId(0L);
        order.setTotalAmount(new BigDecimal("0.0"));
        order.setUserId(userId);
        order.setNickName("未知用户");
        order.setAddress("异常信息" + e.getClass());
        order.setProdctList(null);

        return order;

    }


    //进阶1：远程调用
    public Product getProductFromRemote(Long productId) {
        //1.获取商品服务所在的所有机器的IP和端口
        List<ServiceInstance> instances = discoveryClient.getInstances("service-product");
        ServiceInstance instance = instances.get(0);
        String url = "http://" + instance.getHost() + ":" + instance.getPort() + "/product/" + productId;
        log.info("远程请求: {}", url);
        //2.给远程发送请求
        Product product = restTemplate.getForObject(url, Product.class);
        return product;
    }

    //进阶2：负载均衡
    public Product getProductFromRemoteWithLoadBalance(Long productId) {
        //1.获取商品服务所在的所有机器的IP和端口
/*        List<ServiceInstance> instances = discoveryClient.getInstances("service-product");
        ServiceInstance instance = instances.get(0);*/
        ServiceInstance instance = loadBalancerClient.choose("service-product");
        String url = "http://" + instance.getHost() + ":" + instance.getPort() + "/product/" + productId;
        log.info("远程请求: {}", url);
        //2.给远程发送请求
        Product product = restTemplate.getForObject(url, Product.class);
        return product;
    }

    //进阶3：基于注解的负载均衡
    public Product getProductFromRemoteWithLoadBalanceAnnotation(Long productId) {
        //1.获取商品服务所在的所有机器的IP和端口
/*        List<ServiceInstance> instances = discoveryClient.getInstances("service-product");
        ServiceInstance instance = instances.get(0);*/


        /*ServiceInstance instance = loadBalancerClient.choose("service-product");
        String url ="http://" +instance.getHost() + ":" + instance.getPort()+"/product/"+productId;*/
        String url = "http://service-product/product/" + productId;
//        log.info("远程请求: {}" ,url);

        //2.给远程发送请求
        Product product = restTemplate.getForObject(url, Product.class);
        return product;
    }

}
