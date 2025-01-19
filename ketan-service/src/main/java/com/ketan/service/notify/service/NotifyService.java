package com.ketan.service.notify.service;

public interface NotifyService {
    /**
     * 查询用户未读消息数量
     *
     * @param userId
     * @return
     */
    int queryUserNotifyMsgCount(Long userId);
}
