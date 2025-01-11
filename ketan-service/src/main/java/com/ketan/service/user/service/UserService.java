package com.ketan.service.user.service;

import com.ketan.api.model.vo.user.dto.BaseUserInfoDTO;

public interface UserService {

    /**
     * 查询用户基本信息
     * todo: 可以做缓存优化
     *
     * @param userId
     * @return
     */
    BaseUserInfoDTO queryBasicUserInfo(Long userId);

}
