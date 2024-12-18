package com.ketan.web.global;

import com.ketan.api.model.context.ReqInfoContext;
import com.ketan.core.util.SessionUtil;
import com.ketan.web.config.GlobalViewConfig;
import com.ketan.web.global.vo.GlobalVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * @auther Kindow
 * @date 2024/12/18
 * @project ketan
 * @description
 */

@Service
@Slf4j
public class GlobalInitService {


    @Autowired
    private GlobalViewConfig globalViewConfig;


    public GlobalVo globalAttr() {
        GlobalVo vo = new GlobalVo();
        vo.setSiteInfo(globalViewConfig);
        vo.setIsLogin(false);
        return vo;
    }

    /**
     * 初始化用户信息
     */
    public void initLoginUser(ReqInfoContext.ReqInfo reqInfo) {
        /*
         * 从Spring的RequestContextHolder中获取当前HTTP请求的HttpServletRequest对象。
         * 在Spring中，用于在非Servlet环境（如Spring MVC控制器之外）中访问当前的HTTP请求和响应对象。
         */
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        if (request.getCookies() == null) {
            return;
        }
    }

}
