package com.ketan.service.user.service;

import com.ketan.api.model.vo.user.UserInfoSaveReq;
import com.ketan.api.model.vo.user.dto.BaseUserInfoDTO;
import com.ketan.api.model.vo.user.dto.SimpleUserInfoDTO;
import com.ketan.api.model.vo.user.dto.UserStatisticInfoDTO;

import java.util.Collection;
import java.util.List;

public interface UserService {

    /**
     * 查询用户基本信息
     * todo: 可以做缓存优化
     *
     * @param userId
     * @return
     */
    BaseUserInfoDTO queryBasicUserInfo(Long userId);

    /**
     * 批量查询用户基本信息
     *
     * @param userIds
     * @return
     */
    List<SimpleUserInfoDTO> batchQuerySimpleUserInfo(Collection<Long> userIds);


    /**
     * 查询用户主页信息
     *
     * @param userId
     * @return
     * @throws Exception
     */
    UserStatisticInfoDTO queryUserInfoWithStatistic(Long userId);

    /**
     * 获取登录的用户信息,并更新对应的ip信息
     *
     * @param session  用户会话
     * @param clientIp 用户最新的登录ip
     * @return 返回用户基本信息
     */
    BaseUserInfoDTO getAndUpdateUserIpInfoBySessionId(String session, String clientIp);


    void saveUserInfo(UserInfoSaveReq req);

}
