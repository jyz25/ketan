package com.ketan.web.front.chat.stomp;

import com.ketan.api.model.context.ReqInfoContext;
import com.ketan.service.user.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

/**
 * 握手处理器
 */
@Slf4j
public class AuthHandshakeHandler extends DefaultHandshakeHandler {

    /**
     * determineUser 方法是 DefaultHandshakeHandler 中的一个受保护方法，
     * 用于在握手过程中确定连接的用户身份
     * 为每个 WebSocket 会话确定一个唯一的用户标识
     *
     * @param request
     * @param wsHandler
     * @param attributes 是一个存储在握手过程中的属性映射，通常在握手拦截器或其他预处理步骤中设置。
     * @return Principal 返回的对象代表了一个经过身份验证的用户。
     */
    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        // case1: 根据cookie来识别用户，即可以实现所有用户连相同的ws地址，然后再 AuthHandshakeChannelInterceptor 中进行destination的转发
        ReqInfoContext.ReqInfo reqInfo = (ReqInfoContext.ReqInfo) attributes.get(LoginService.SESSION_KEY);
        if (reqInfo != null) {
            return reqInfo;
        }

        // case2: 根据路径来区分用户
        // 获取例如 ws://localhost/gpt/id 订阅地址中的最后一个用户 id 参数作为用户的标识, 为实现发送信息给指定用户做准备
        String uri = request.getURI().toString();
        String uid = uri.substring(uri.lastIndexOf("/") + 1);
        log.info("{} -> {}", uri, uid);
        return () -> uid;
    }
}
