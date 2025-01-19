package com.ketan.web.global;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.ketan.api.model.context.ReqInfoContext;
import com.ketan.api.model.vo.seo.Seo;
import com.ketan.api.model.vo.user.dto.BaseUserInfoDTO;
import com.ketan.core.util.NumUtil;
import com.ketan.core.util.SessionUtil;
import com.ketan.service.notify.service.NotifyService;
import com.ketan.service.sitemap.service.SitemapService;
import com.ketan.service.statistics.service.UserStatisticService;
import com.ketan.service.user.service.LoginService;
import com.ketan.service.user.service.UserService;
import com.ketan.web.config.GlobalViewConfig;
import com.ketan.web.global.vo.GlobalVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
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

    @Value("${env.name}")
    private String env;

    @Autowired
    private UserService userService;

    @Resource
    private NotifyService notifyService;

    @Autowired
    private GlobalViewConfig globalViewConfig;

    @Autowired
    private SitemapService sitemapService;

    @Autowired
    private SeoInjectService seoInjectService;


    @Autowired
    private UserStatisticService userStatisticService;

    /**
     * 全局属性配置
     */
    public GlobalVo globalAttr() {
        GlobalVo vo = new GlobalVo();
        vo.setEnv(env);
        vo.setSiteInfo(globalViewConfig);
        vo.setOnlineCnt(userStatisticService.getOnlineUserCnt());
        vo.setSiteStatisticInfo(sitemapService.querySiteVisitInfo(null, null));
        vo.setTodaySiteStatisticInfo(sitemapService.querySiteVisitInfo(LocalDate.now(), null));

        if (ReqInfoContext.getReqInfo() == null || ReqInfoContext.getReqInfo().getSeo() == null || CollectionUtils.isEmpty(ReqInfoContext.getReqInfo().getSeo().getOgp())) {
            Seo seo = seoInjectService.defaultSeo();
            vo.setOgp(seo.getOgp());
            vo.setJsonLd(JSONUtil.toJsonStr(seo.getJsonLd()));
        } else {
            Seo seo = ReqInfoContext.getReqInfo().getSeo();
            vo.setOgp(seo.getOgp());
            vo.setJsonLd(JSONUtil.toJsonStr(seo.getJsonLd()));
        }

        try {
            if (ReqInfoContext.getReqInfo() != null && NumUtil.upZero(ReqInfoContext.getReqInfo().getUserId())) {
                vo.setIsLogin(true);
                vo.setUser(ReqInfoContext.getReqInfo().getUser());
                vo.setMsgNum(ReqInfoContext.getReqInfo().getMsgNum());
            } else {
                vo.setIsLogin(false);
            }

            HttpServletRequest request =
                    ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            if (request.getRequestURI().startsWith("/column")) {
                vo.setCurrentDomain("column");
            } else if (request.getRequestURI().startsWith("/chat")) {
                vo.setCurrentDomain("chat");
            } else {
                vo.setCurrentDomain("article");
            }
        } catch (Exception e) {
            log.error("loginCheckError:", e);
        }
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
        Optional.ofNullable(SessionUtil.findCookieByName(request, LoginService.SESSION_KEY))
                .ifPresent(cookie -> initLoginUser(cookie.getValue(), reqInfo));
    }

    public void initLoginUser(String session, ReqInfoContext.ReqInfo reqInfo) {
        BaseUserInfoDTO user = userService.getAndUpdateUserIpInfoBySessionId(session, null);
        reqInfo.setSession(session);
        if (user != null) {
            reqInfo.setUserId(user.getUserId());
            reqInfo.setUser(user);
            reqInfo.setMsgNum(notifyService.queryUserNotifyMsgCount(user.getUserId()));
        }
    }

}
