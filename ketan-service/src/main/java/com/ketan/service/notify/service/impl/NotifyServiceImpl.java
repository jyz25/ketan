package com.ketan.service.notify.service.impl;

import com.ketan.api.model.enums.NotifyStatEnum;
import com.ketan.service.notify.repository.dao.NotifyMsgDao;
import com.ketan.service.notify.service.NotifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class NotifyServiceImpl implements NotifyService {

    @Autowired
    private NotifyMsgDao notifyMsgDao;

    @Override
    public int queryUserNotifyMsgCount(Long userId) {
        return notifyMsgDao.countByUserIdAndStat(userId, NotifyStatEnum.UNREAD.getStat());
    }
}
