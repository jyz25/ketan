package com.ketan.core.ws;


import com.ketan.api.model.context.ReqInfoContext;
import com.ketan.core.mdc.MdcUtil;
import com.ketan.core.util.SpringUtil;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;

/**
 * websocket消息响应封装工具类
 */
public class WebSocketResponseUtil {
    private static volatile SimpMessagingTemplate simpMessagingTemplate;

    /**
     * 初始化
     */
    private static void initSimpMessageTemplate() {
        if (simpMessagingTemplate == null) {
            synchronized (WebSocketResponseUtil.class) {
                if (simpMessagingTemplate == null) {
                    simpMessagingTemplate = SpringUtil.getBean(SimpMessagingTemplate.class);
                }
            }
        }
    }

    /**
     * 给用户发送消息
     *
     * @param user        用户
     * @param destination 用户订阅地址
     * @param data        消息实体
     */
    public static void sendMsgToUser(String user, String destination, Object data) {
        initSimpMessageTemplate();
        simpMessagingTemplate.convertAndSendToUser(user, destination, data);
    }

    /**
     * 消息广播
     *
     * @param destination 订阅地址
     * @param data        消息实体
     */
    public static void broadcastMsg(String destination, Object data) {
        initSimpMessageTemplate();
        simpMessagingTemplate.convertAndSend(destination, data);
    }

    /**
     * 封装websocket的消息处理，主要是设置上下文，全链路traceId
     *
     * @param accessor 请求
     * @param func     执行体
     */
    public static void execute(SimpMessageHeaderAccessor accessor, Runnable func) {
        try {
            ReqInfoContext.ReqInfo reqInfo = (ReqInfoContext.ReqInfo) accessor.getUser();
            ReqInfoContext.addReqInfo(reqInfo);
            String traceId = (String) accessor.getSessionAttributes().get(MdcUtil.TRACE_ID_KEY);
            MdcUtil.add(MdcUtil.TRACE_ID_KEY, traceId);


            // 执行具体的业务逻辑
            func.run();

        } finally {
            ReqInfoContext.clear();
            MdcUtil.clear();
        }
    }
}
