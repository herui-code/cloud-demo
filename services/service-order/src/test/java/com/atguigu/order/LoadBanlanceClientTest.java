package com.atguigu.order;

import net.bytebuddy.utility.nullability.AlwaysNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;

@SpringBootTest
public class LoadBanlanceClientTest {

    @Autowired
    LoadBalancerClient loadBalancerClient;

    @Test
    void test(){
        ServiceInstance instance = loadBalancerClient.choose("service-product");
        System.out.println("instance"+instance.getHost() + ":" + instance.getPort());
        instance = loadBalancerClient.choose("service-product");
        System.out.println("instance"+instance.getHost() + ":" + instance.getPort());
        instance = loadBalancerClient.choose("service-product");
        System.out.println("instance"+instance.getHost() + ":" + instance.getPort());
        instance = loadBalancerClient.choose("service-product");
        System.out.println("instance"+instance.getHost() + ":" + instance.getPort());
        instance = loadBalancerClient.choose("service-product");
        System.out.println("instance"+instance.getHost() + ":" + instance.getPort());
        instance = loadBalancerClient.choose("service-product");
        System.out.println("instance"+instance.getHost() + ":" + instance.getPort());
        instance = loadBalancerClient.choose("service-product");
        System.out.println("instance"+instance.getHost() + ":" + instance.getPort());


    }
}
