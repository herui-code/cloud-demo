package com.atguigu.order.exception;

import com.alibaba.csp.sentinel.adapter.spring.webmvc_v6x.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.atguigu.common.R;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;

@Component
public class MyExceptionHandler implements BlockExceptionHandler {

    ObjectMapper mapper = new ObjectMapper();

    /**
     *
     * @param httpServletRequest
     * @param httpServletResponse
     * @param s 资源名
     * @param e 异常
     * @throws Exception
     */
    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, String s, BlockException e) throws Exception {
        PrintWriter writer = httpServletResponse.getWriter();
        httpServletResponse.setContentType("application/json;charset=UTF-8");
        httpServletResponse.setStatus(429);//429表示太多的请求 too many request
        writer.write(97);

        R error = R.error(500, s + "被sentinel限制了，原因：" + e.getClass());
        String s1 = mapper.writeValueAsString(error);
        writer.write(s1);
        writer.flush();
        writer.close();



    }
}
