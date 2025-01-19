package com.ketan.service.user.service;

public interface LoginService {
    String SESSION_KEY = "f-session";
    String USER_DEVICE_KEY = "f-device";


    /**
     * 用户名密码方式登录
     *
     * @param username 用户名
     * @param password 密码
     * @return
     */
    String loginByUserPwd(String username, String password);

    /**
     * 登出
     *
     * @param session 用户会话
     */
    void logout(String session);

    /**
     * 适用于微信公众号登录场景下，自动注册一个用户
     *
     * @param uuid 微信唯一标识
     * @return userId 用户主键
     */
    Long autoRegisterWxUserInfo(String uuid);


    /**
     * 给微信公众号的用户生成一个用于登录的会话
     *
     * @param userId 用户主键id
     * @return
     */
    String loginByWx(Long userId);
}
