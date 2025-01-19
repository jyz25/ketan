package com.ketan.service.user.service;


import com.ketan.api.model.vo.user.UserPwdLoginReq;

/**
 * 用户注册服务
 *
 * @author YiHui
 * @date 2023/6/26
 */
public interface RegisterService {

    /**
     * 通过微信公众号进行注册
     *
     * @param thirdAccount
     * @return
     */
    Long registerByWechat(String thirdAccount);
}
