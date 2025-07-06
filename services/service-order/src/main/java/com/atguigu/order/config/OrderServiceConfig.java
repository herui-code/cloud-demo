package com.atguigu.order.config;


import feign.Logger;
import feign.Retryer;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;

@Configuration
public class OrderServiceConfig {

    @LoadBalanced
    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }


    @Bean
    Logger.Level feignLoggerLevel(){
        return Logger.Level.FULL;
    }

//    @Bean
    Retryer retryer(){

        return new Retryer.Default();
    }

}
