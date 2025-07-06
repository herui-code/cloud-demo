package com.atguigu.gateway.predicate;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.cloud.gateway.handler.predicate.AbstractRoutePredicateFactory;
import org.springframework.cloud.gateway.handler.predicate.GatewayPredicate;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ServerWebExchange;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
@Component
public class VipRoutePredicateFactory extends AbstractRoutePredicateFactory<VipRoutePredicateFactory.Config> {

    public static final String PARAM_KEY = "param";
    public static final String VALUE_KEY = "value";
    public VipRoutePredicateFactory() {
        super(Config.class);
    }

//    短格式的属性顺序
    public List<String> shortcutFieldOrder() {
        return Arrays.asList(PARAM_KEY, VALUE_KEY);
    }


    /*
    * 传入参数必须满足的算法
    * */
    @Override
    public Predicate<ServerWebExchange> apply(Config config) {
        return new GatewayPredicate() {
            //localhost/search?q=haha&&user=admins
            @Override
            public boolean test(ServerWebExchange serverWebExchange) {
                ServerHttpRequest request = serverWebExchange.getRequest();
                String param = request.getQueryParams().getFirst(config.getParam());
                if(StringUtils.hasText(param)&&param.equals(config.getValue())){
                    return true;
                }
                return false;
            }
        };
    }

    @Validated
    public static class Config{
        @NotEmpty
        private String param;
        @NotEmpty
        private String value;
        public String getParam() {
            return param;
        }
        public String getValue() {
            return value;
        }

        public void setParam(String param) {
            this.param = param;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

}
