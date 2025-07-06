package com.atguigu.order.properties;


import com.atguigu.order.service.OrderService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "order")
public class OrderProperties {

    String timeout;
    String autoConfirm;
    String dbUrl;
}
