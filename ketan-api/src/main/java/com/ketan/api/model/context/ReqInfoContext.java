package com.ketan.api.model.context;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.ketan.api.model.vo.seo.Seo;
import com.ketan.api.model.vo.user.dto.BaseUserInfoDTO;
import lombok.Data;

import java.security.Principal;

/**
 * @auther Kindow
 * @date 2024/12/18
 * @project ketan
 * @description 请求上下文，携带用户身份相关信息
 */
public class ReqInfoContext {

    // 与线程挂钩，一个HTTP请求等于一个线程，对contexts的操作会局限于该线程对应的虚拟机栈
    // 所以一个线程有一份该内存用于放置请求信息，TransmittableThreadLocal是ThreadLocal的增强版
    private static TransmittableThreadLocal<ReqInfo> contexts = new TransmittableThreadLocal<>();

    // 在请求拦截器中被调用
    public static void addReqInfo(ReqInfo reqInfo) {
        contexts.set(reqInfo);
    }

    public static void clear() {
        contexts.remove();
    }

    public static ReqInfo getReqInfo() {
        return contexts.get();
    }


    @Data
    public static class ReqInfo implements Principal {

        /**
         * 用户id
         */
        private Long userId;

        /**
         * 登录的会话
         */
        private String session;

        private String deviceId;

        /**
         * post 表单参数
         */
        private String payload;

        /**
         * 访问的域名
         */
        private String host;

        /**
         * 设备信息
         */
        private String userAgent;

        /**
         * 客户端ip
         */
        private String clientIp;

        /**
         * referer
         */
        private String referer;

        /**
         * 访问路径
         */
        private String path;

        /**
         * 消息数量
         */
        private Integer msgNum;

        private Seo seo;

        /**
         * 用户信息
         */
        private BaseUserInfoDTO user;

        @Override
        public String getName() {
            return session;
        }
    }

}
