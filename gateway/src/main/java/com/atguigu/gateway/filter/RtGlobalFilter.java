package com.atguigu.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
//日志在lombok依赖中
@Slf4j
@Component
public class RtGlobalFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //exchange里面封装了本次的请求
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        String path = request.getURI().toString();
        long start = System.currentTimeMillis();
        log.info("请求【{}】开始: 时间:{}", path,start);
        //以上是前置逻辑

        //下面 请求放行
        //Mono是响应式编程里面，封装了一个或0个数据的响应式流
        ////放行
        Mono<Void> filter = chain.filter(exchange)
                .doFinally(//这里面才是真正的后置逻辑
                s -> {
                    long end = System.currentTimeMillis();
                    log.info("请求【{}】结束: 时间:{} 耗时:{}", path, end,end - start);
                }
        );

        //后置逻辑,由于异步编程，不会等“    Mono<Void> filter = chain.filter(exchange);//”运行结束就会执行后面的逻辑。所以
        //只能调用响应式的api

        return filter;
    }


    @Override
    public int getOrder() {
        return -1;
    }
}
