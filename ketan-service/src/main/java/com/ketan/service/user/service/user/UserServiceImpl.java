package com.ketan.service.user.service.user;

import com.ketan.api.model.exception.ExceptionUtil;
import com.ketan.api.model.vo.constants.StatusEnum;
import com.ketan.api.model.vo.user.dto.BaseUserInfoDTO;
import com.ketan.service.user.converter.UserConverter;
import com.ketan.service.user.repository.dao.UserDao;
import com.ketan.service.user.repository.entity.UserInfoDO;
import com.ketan.service.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserServiceImpl implements UserService {


    @Autowired
    private UserDao userDao;

    @Override
    public BaseUserInfoDTO queryBasicUserInfo(Long userId) {
        UserInfoDO user = userDao.getByUserId(userId);
        if (user == null) {
            throw ExceptionUtil.of(StatusEnum.USER_NOT_EXISTS, "userId=" + userId);
        }
        return UserConverter.toDTO(user);
    }
}
