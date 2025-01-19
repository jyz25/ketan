package com.ketan.service.user.service.user;

import com.ketan.api.model.enums.NotifyTypeEnum;
import com.ketan.api.model.enums.user.LoginTypeEnum;
import com.ketan.api.model.exception.ExceptionUtil;
import com.ketan.api.model.vo.constants.StatusEnum;
import com.ketan.api.model.vo.notify.NotifyMsgEvent;
import com.ketan.api.model.vo.user.UserPwdLoginReq;
import com.ketan.core.util.SpringUtil;
import com.ketan.core.util.TransactionUtil;
import com.ketan.service.user.repository.dao.UserDao;
import com.ketan.service.user.repository.entity.UserDO;
import com.ketan.service.user.repository.entity.UserInfoDO;
import com.ketan.service.user.service.RegisterService;
import com.ketan.service.user.service.help.UserPwdEncoder;
import com.ketan.service.user.service.help.UserRandomGenHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户注册服务
 *
 * @author YiHui
 * @date 2023/6/26
 */
@Service
public class RegisterServiceImpl implements RegisterService {
    @Autowired
    private UserPwdEncoder userPwdEncoder;
    @Autowired
    private UserDao userDao;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long registerByWechat(String thirdAccount) {
        // 用户不存在，则需要注册
        // 1. 保存用户登录信息
        UserDO user = new UserDO();
        user.setThirdAccountId(thirdAccount);
        user.setLoginType(LoginTypeEnum.WECHAT.getType());
        userDao.saveUser(user);


        // 2. 初始化用户信息，随机生成用户昵称 + 头像
        UserInfoDO userInfo = new UserInfoDO();
        userInfo.setUserId(user.getId());
        userInfo.setUserName(UserRandomGenHelper.genNickName());
        userInfo.setPhoto(UserRandomGenHelper.genAvatar());
        userDao.save(userInfo);

        processAfterUserRegister(user.getId());
        return user.getId();
    }


    /**
     * 用户注册完毕之后触发的动作
     *
     * @param userId
     */
    private void processAfterUserRegister(Long userId) {
        TransactionUtil.registryAfterCommitOrImmediatelyRun(new Runnable() {
            @Override
            public void run() {
                // 用户注册事件
                SpringUtil.publishEvent(new NotifyMsgEvent<>(this, NotifyTypeEnum.REGISTER, userId));
            }
        });
    }
}
