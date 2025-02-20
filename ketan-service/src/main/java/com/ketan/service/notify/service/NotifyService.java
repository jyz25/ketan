package com.ketan.service.notify.service;

import com.ketan.api.model.enums.NotifyTypeEnum;
import com.ketan.api.model.vo.PageListVo;
import com.ketan.api.model.vo.PageParam;
import com.ketan.api.model.vo.notify.dto.NotifyMsgDTO;
import com.ketan.service.user.repository.entity.UserFootDO;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

import java.util.Map;

public interface NotifyService {

    public static String NOTIFY_TOPIC = "/msg";
    /**
     * 查询用户未读消息数量
     *
     * @param userId
     * @return
     */
    int queryUserNotifyMsgCount(Long userId);


    /**
     * 保存通知
     *
     * @param foot
     * @param notifyTypeEnum
     */
    void saveArticleNotify(UserFootDO foot, NotifyTypeEnum notifyTypeEnum);

    /**
     * 查询通知列表
     *
     * @param userId
     * @param type
     * @param page
     * @return
     */
    PageListVo<NotifyMsgDTO> queryUserNotices(Long userId, NotifyTypeEnum type, PageParam page);


    /**
     * 查询未读消息数
     * @param userId
     * @return
     */
    Map<String, Integer> queryUnreadCounts(long userId);


    // -------------------------------------------- 下面是与用户的websocket长连接维护相关实现 -------------------------

    /**
     * ws: 给用户发送消息通知
     *
     * @param userId 用户id
     * @param msg    通知内容
     */
    void notifyToUser(Long userId, String msg);

    /**
     * ws: 维护与用户的长连接通道
     *
     * @param accessor
     */
    void notifyChannelMaintain(StompHeaderAccessor accessor);

}
