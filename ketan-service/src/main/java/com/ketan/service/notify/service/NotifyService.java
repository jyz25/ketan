package com.ketan.service.notify.service;

import com.ketan.api.model.enums.NotifyTypeEnum;
import com.ketan.service.user.repository.entity.UserFootDO;

public interface NotifyService {
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

}
