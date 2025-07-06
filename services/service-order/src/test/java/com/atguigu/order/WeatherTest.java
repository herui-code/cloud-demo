package com.atguigu.order;

import com.atguigu.order.feign.WeatherFeignClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class WeatherTest {

    @Autowired
    WeatherFeignClient weatherFeignClient;

    @Test
    void test(){
        String auth="APPCODE 93b7e19861a24c519a7548b17dc16d75";
        String token="50b53ff8dd7d9fa320d3d3ca32cf8ed1";
        String cityId="2182";
        String weather = weatherFeignClient.getWeather(auth, token, cityId);
        System.out.println(weather);
    }
}
