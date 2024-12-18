package com.ketan.web.hook.interceptor;

import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.ketan.api.model.context.ReqInfoContext;
import com.ketan.web.global.GlobalInitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @auther Kindow
 * @date 2024/12/18
 * @project ketan
 * @description 视图拦截器
 */

@Slf4j
@Component
public class GlobalViewInterceptor implements AsyncHandlerInterceptor {

    @Autowired
    private GlobalInitService globalInitService;

    // 控制器方法调用之前执行
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return AsyncHandlerInterceptor.super.preHandle(request, response, handler);
    }


    // 在控制器方法调用之后、视图渲染之前执行。
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if(!ObjectUtils.isEmpty(modelAndView)){
            modelAndView.getModel().put("global",globalInitService.globalAttr());
        }
    }
}
