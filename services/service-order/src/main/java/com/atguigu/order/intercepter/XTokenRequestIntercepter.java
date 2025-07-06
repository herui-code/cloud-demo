package com.atguigu.order.intercepter;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;
@Component
public class XTokenRequestIntercepter implements RequestInterceptor {

    /**
     *
     * @param requestTemplate 这次请求的详细信息封装到了reqquestTemplate中
     */
    @Override
    public void apply(RequestTemplate requestTemplate) {
        System.out.println("XTokenRequestIntercepter ..........启动" );
        requestTemplate.header("X-Token", UUID.randomUUID().toString());
    }
}
