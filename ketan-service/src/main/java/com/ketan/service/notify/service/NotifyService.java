package com.ketan.service.notify.service;

import com.ketan.api.model.enums.NotifyTypeEnum;
import com.ketan.api.model.vo.PageListVo;
import com.ketan.api.model.vo.PageParam;
import com.ketan.api.model.vo.notify.dto.NotifyMsgDTO;
import com.ketan.service.user.repository.entity.UserFootDO;

import java.util.Map;

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

}
