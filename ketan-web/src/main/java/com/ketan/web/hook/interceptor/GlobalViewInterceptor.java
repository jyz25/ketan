package com.ketan.web.hook.interceptor;

import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.ketan.api.model.context.ReqInfoContext;
import com.ketan.api.model.vo.ResVo;
import com.ketan.api.model.vo.constants.StatusEnum;
import com.ketan.core.permission.Permission;
import com.ketan.core.permission.UserRole;
import com.ketan.core.util.JsonUtil;
import com.ketan.core.util.SpringUtil;
import com.ketan.service.rank.service.UserActivityRankService;
import com.ketan.service.rank.service.model.ActivityScoreBo;
import com.ketan.web.global.GlobalInitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
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

    /**
     * DispatcherServlet 负责将请求分发到具体的控制器方法。
     * 在请求到达控制器方法之前，会调用 AsyncHandlerInterceptor 的 preHandle 方法。
     * 控制器方法调用之前执行 在WebFilter之后执行
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Permission permission = handlerMethod.getMethod().getAnnotation(Permission.class);
            if (permission == null) {
                permission = handlerMethod.getBeanType().getAnnotation(Permission.class);
            }

            if (permission == null || permission.role() == UserRole.ALL) {
                if (ReqInfoContext.getReqInfo() != null) {
                    // 用户活跃度更新
                    SpringUtil.getBean(UserActivityRankService.class).addActivityScore(ReqInfoContext.getReqInfo().getUserId(), new ActivityScoreBo().setPath(ReqInfoContext.getReqInfo().getPath()));
                }
                return true;
            }

            if (ReqInfoContext.getReqInfo() == null || ReqInfoContext.getReqInfo().getUserId() == null) {
                if (handlerMethod.getMethod().getAnnotation(ResponseBody.class) != null
                        || handlerMethod.getMethod().getDeclaringClass().getAnnotation(RestController.class) != null) {
                    // 访问需要登录的rest接口
                    response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
                    response.getWriter().println(JsonUtil.toStr(ResVo.fail(StatusEnum.FORBID_NOTLOGIN)));
                    response.getWriter().flush();
                    return false;
                } else if (request.getRequestURI().startsWith("/api/admin/") || request.getRequestURI().startsWith("/admin/")) {
                    response.sendRedirect("/admin");
                } else {
                    // 访问需要登录的页面时，直接跳转到登录界面
                    response.sendRedirect("/");
                }
                return false;
            }
            if (permission.role() == UserRole.ADMIN && !UserRole.ADMIN.name().equalsIgnoreCase(ReqInfoContext.getReqInfo().getUser().getRole())) {
                // 设置为无权限
                response.setStatus(HttpStatus.FORBIDDEN.value());
                return false;
            }
        }
        return true;
    }


    // 在控制器方法调用之后、视图渲染之前执行。
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (!ObjectUtils.isEmpty(modelAndView)) {
            modelAndView.getModel().put("global", globalInitService.globalAttr());
        }
    }
}
