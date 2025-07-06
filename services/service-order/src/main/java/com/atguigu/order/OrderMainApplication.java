package com.atguigu.order;

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

import javax.swing.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/*
* sentinel 新增流控规则
* 1.资源名：1）sentinel自动探测的接口名 /create ;2） @SentinelResource标注的任意方法；3）openfeign 远程调用的完整描述 Get:http://service-product/product/{od}
* 2.针对来源：default表示无论从什么地方来的访问请求，都遵循定义的流控规则
* 3.阈值类型：
*  1）QPS:每秒访问的请求数    单机阈值：1 - 表示每秒通过1个请求，多余请求将被丢弃
*  原理：使用计数器统计请求数量
*  2）并发线程数：当前资源同时正在处理的最大线程数量。用于控制系统中某个资源的并发访问量，防止资源耗尽（如数据库连接池、线程池等）。
*   # 当前资源最多允许 10 个并发线程同时执行
      threshold: 10
      strategy: thread
  *
* 4.是否集群：勾选后，代表在集群环境进行统计（这里集群指的是同一个微服务有多个副本）
*   单机均摊：微服务集群中，微服务的每一个副本，单位时间内可以访问的数量都是 单机阈值定义的数量，这里定义的是1，表示每一个微服务单位时间内都可以被访问1次，总共集群可以通过副本数量*单机阈值定义的数量，进行访问控制
*   总体阈值：表示集群中所有的微服务副本合起来只能通过”单机阈值"定义的请求数量；比如 单机阈值为1，则集群中微服务副本合起来只能通过1次请求；
*
* 5.高级选项
*  1）流控模式
*   直接（默认）：对资源进行直接控制，超过了规则限定的请求数量的请求，将被直接丢弃
*
*   关联：将两个资源进行关联。对关联资源进行限流，只有当另一个资源的请求数量超过规则限定，则对关联资源进行限流
*   关联会多出一个“关联资源”
*   关联资源：定义关联的资源，比如对/readDb定义规则，关联资源填写 /writeDb，效果为当/writeDb有大量的请求时候，对/readDb进行限流，限流规则为 个体阈值定义的数据
*           如果/writeDb没有太多请求时候，对/readDb不进行限流
*
*   链路：根据不同的请求链路进行流量控制；比如a调用b，规则定义限制b,但是炸的是a
*       比如：访问service-product服务有两个链路；一个是order ——> product  不用遵循sentinel规则 ；另一个是 seckill ——> product 遵循sentinel规则
*       具体表现效果为：控制后面的服务，前面服务“炸”；比如控制product服务，炸 seckill服务
*       链路会多出一个“入口资源”选项
*       入口资源：规则限定资源的前一个链路资源。比如/seckill调用createProduct;对createProduct的指定限流规则，入口资源写/seckill。
*               效果为对seckill会调用createProduct，createProduct限制了seckill每秒的请求数量，seckill请求数量超过了单机阈值，就会报错
*
* 6.流控效果 （只有快速失败支持流控模式，直接，关联，链路）
* 1） 快速失败(直接拒绝)：指的是如果没有超过设置的单机阈值，则直接通过，如果超过了单机阈值，则直接报错，不会进行后续的流控规则判断
*     比如：每秒只允许通过1个请求。如果1秒内有多个请求，则只有1个请求访问通过，其他请求全部抛出异常（也可以是自定义的BlockException）
* 2） Warm Up：预热/冷启动。让流量进入系统时，让流量缓慢进入，避免流量瞬间进入系统，导致系统崩溃。如果超高峰流量到达，允许通过的请求数量由每秒3个增加到每秒5个，到每秒10个，然后到达峰值20.
*             让系统逐步的增加自己的处理能力，以适应突然达到的高峰请求。
*     warm up设置项：1.QPS:每秒通过的请求数量 2.period：周期，冷启动周期是多少。在这个周期时间内，QPS达到设置的峰值
*
* 3） 排队等待：QPS-将放行的请求均摊到每秒的时间内，统计时间是基于毫秒，不支持QPS大于1000，因为每秒只有1000毫秒
*             timeout（单位时间毫秒）:规定允许排队等待的时间，如果排队等待的时间超过了timeout,则后续的任然在排队的请求会直接丢弃
*     比如:QPS=2,那么1000/2=500毫秒。则没500毫秒放行一个请求。多余的请求会排队等待，排队等待的请求，会按照队列的顺序进行访问，如果队列中还有请求，则按照队列的顺序进行访问，如果队列中没有请求，则按照QPS的规则进行访问。
*
* */

/*
* 熔断：熔断是保护自身，所以定义在客户端
*
* 熔断规则：
* 1.资源名称：1）sentinel自动探测的接口名 /create ;2） @SentinelResource标注的任意方法；3）openfeign 远程调用的完整描述 Get:http://service-product/product/{od}
*
* 2.熔断策略：1）慢调用比例 2）异常比例(异常比例会没有最大RT选项) 3）异常数（不计算比例，计算具体次数）
*
* 3.最大RT（response time,最大响应时间）
*
* 4,比例阈值：熔断比例，范围是0到1.比如：熔断比例0.8，表示如果超过80%的请求都是（慢请求，异常请求或异常数请求），触发熔断
*
* 5.熔断时长：熔断的时间。熔断时长结束之后，熔断器会自动关闭，进入半熔断状态。
*
* 6，最小请求数：必须达到的最小请求数量，才会开始熔断的统计。
*
* 7.统计时长：表示用设置的“统计时长”作为时间，统计在这个时间范围内的请求是否超过比例阈值，如果超过，则触发熔断
*
*
* */

/*
* 熔断规则举例：
* 1.满请求
* 1）熔断策略：漫调用比例  *** 最大RT 1000ms,表示超过1000毫秒没有返回的请求为满慢调用请求
*            比例阈值：0.8，表示如果80%的请求为慢请求，则触发熔断
*            熔断时长：30s,表示断路器打开30s,这30s内，不进行远程调用，直接返回兜底方法
*            最小请求数：5.必须达到5个请求数量，才开始统计
*            统计时长：5000ms.表示每5秒统计一次，如果5秒内，有80%的请求慢，则触发熔断
*
* 2）熔断策略： 异常数（***没有最大RT设置项）
 *            比例阈值：0.8，表示如果80%的请求返回异常数据，比如500状态码，则触发熔断
 *            熔断时长：30s,表示断路器打开30s,这30s内，不进行远程调用，直接返回兜底方法
 *            最小请求数：5.必须达到5个请求数量，才开始统计
 *            统计时长：5000ms.表示每5秒统计一次，如果5秒内，有80%的请求慢，则触发熔断
 * 3）熔断策略：异常数
 *            ***异常数：n；表示只要有n个远程调用返回异常，就触发熔断。不计算比例
 *            熔断时长：30s,表示断路器打开30s,这30s内，不进行远程调用，直接返回兜底方法
 *            最小请求数：5.必须达到5个请求数量，才开始统计
 *            统计时长：5000ms.表示每5秒统计一次，如果5秒内，有n个异常返回，则触发熔断
*
* */

@EnableFeignClients//开启openfeign远程调用功能
@SpringBootApplication
@EnableDiscoveryClient//开启服务发现功能
public class OrderMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderMainApplication.class,args);
    }
    @Bean
    ApplicationRunner runner(NacosConfigManager nacosConfigManager){
        return new ApplicationRunner() {
            @Override
            public void run(ApplicationArguments args) throws Exception {
                ConfigService configService = nacosConfigManager.getConfigService();
                configService.addListener("service-order.properties", "DEFAULT_GROUP",
                        new Listener() {
                            @Override
                            public Executor getExecutor() {
                                return Executors.newFixedThreadPool(4);
                            }

                            @Override
                            public void receiveConfigInfo(String configInfo) {
                                System.out.println("变化的配置信息:"+configInfo);
                            }
                        });
                System.out.println("=================");
            }
        };
    }
}
