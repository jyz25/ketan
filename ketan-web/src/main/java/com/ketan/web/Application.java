package com.ketan.web;


import com.ketan.web.global.ForumExceptionHandler;
import com.ketan.web.hook.interceptor.GlobalViewInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;


/**
 * @auther Kindow
 * @date 2024/12/11
 * @project ketan
 * @description 主启动类
 */

@ServletComponentScan // 注入 OnlineUserCountListener
@SpringBootApplication
public class Application implements WebMvcConfigurer {

    @Autowired
    private GlobalViewInterceptor globalViewInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(globalViewInterceptor).addPathPatterns("/**");
    }

    @Override
    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
        resolvers.add(0, new ForumExceptionHandler());
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
