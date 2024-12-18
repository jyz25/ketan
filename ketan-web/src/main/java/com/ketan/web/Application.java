package com.ketan.web;


import com.ketan.web.hook.interceptor.GlobalViewInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/**
 * @auther Kindow
 * @date 2024/12/11
 * @project ketan
 * @description 主启动类
 */

@SpringBootApplication
public class Application implements WebMvcConfigurer {

    @Autowired
    private GlobalViewInterceptor globalViewInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(globalViewInterceptor).addPathPatterns("/**");
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
