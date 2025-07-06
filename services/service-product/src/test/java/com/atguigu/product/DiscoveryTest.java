package com.atguigu.product;

import com.alibaba.cloud.nacos.discovery.NacosServiceDiscovery;
import com.alibaba.nacos.api.exception.NacosException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;

import java.util.List;

@SpringBootTest
public class DiscoveryTest {

    @Autowired
    DiscoveryClient discoveryClient;

    @Autowired
    NacosServiceDiscovery nacosServiceDiscovery;



    @Test
    void discoveryclientTest(){

        for(String serviceName : discoveryClient.getServices()){
            System.out.println("service = " + serviceName);

            //获取IP+端口号
            List<ServiceInstance> instances = discoveryClient.getInstances(serviceName);
            for(ServiceInstance instance : instances){
                System.out.println("instance = " + instance.getHost() + ":" + instance.getPort());
            }

        }

    }

    @Test
    public void nacosServiceDiscoveryTest() throws NacosException {
        for(String serviceName : nacosServiceDiscovery.getServices()){
            System.out.println("service:" + serviceName);

            List<ServiceInstance> instances = nacosServiceDiscovery.getInstances(serviceName);
            for (ServiceInstance instance : instances){
                System.out.println("instance :" + instance.getHost()+ ":" + instance.getPort());
            }
        }
    }

}
