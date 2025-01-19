package com.ketan.service.user.service.user;

import com.ketan.api.model.context.ReqInfoContext;
import com.ketan.api.model.exception.ExceptionUtil;
import com.ketan.api.model.vo.constants.StatusEnum;
import com.ketan.api.model.vo.user.UserPwdLoginReq;
import com.ketan.service.user.repository.dao.UserDao;
import com.ketan.service.user.repository.entity.UserDO;
import com.ketan.service.user.service.LoginService;
import com.ketan.service.user.service.help.UserPwdEncoder;
import com.ketan.service.user.service.help.UserSessionHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 基于验证码、用户名密码的登录方式
 */
@Service
@Slf4j
public class LoginServiceImpl implements LoginService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserPwdEncoder userPwdEncoder;

    @Autowired
    private UserSessionHelper userSessionHelper;


    /**
     * 用户名密码方式登录
     *
     * @param username 用户名
     * @param password 密码
     * @return
     */
    @Override
    public String loginByUserPwd(String username, String password) {
        UserDO user = userDao.getUserByUserName(username);
        if (user == null) {
            throw ExceptionUtil.of(StatusEnum.USER_NOT_EXISTS, "userName=" + username);
        }

        if (!userPwdEncoder.match(password, user.getPassword())) {
            throw ExceptionUtil.of(StatusEnum.USER_PWD_ERROR);
        }

        Long userId = user.getId();
/*
    打算取消AI功能，懒得搞了
        // 1. 为了兼容历史数据，对于首次登录成功的用户，初始化ai信息
        userAiService.initOrUpdateAiInfo(new UserPwdLoginReq().setUserId(userId).setUsername(username).setPassword(password));
*/

        // 登录成功，返回对应的session
        ReqInfoContext.getReqInfo().setUserId(userId);
        return userSessionHelper.genSession(userId);
    }

    @Override
    public void logout(String session) {
        userSessionHelper.removeSession(session);
    }
}
