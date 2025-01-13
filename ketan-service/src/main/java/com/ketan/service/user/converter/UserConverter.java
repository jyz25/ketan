package com.ketan.service.user.converter;


import com.ketan.api.model.enums.RoleEnum;
import com.ketan.api.model.vo.user.dto.BaseUserInfoDTO;
import com.ketan.api.model.vo.user.dto.SimpleUserInfoDTO;
import com.ketan.api.model.vo.user.dto.UserStatisticInfoDTO;
import com.ketan.service.user.repository.entity.UserInfoDO;
import org.springframework.beans.BeanUtils;

/**
 * 用户转换
 *
 */
public class UserConverter {
    public static BaseUserInfoDTO toDTO(UserInfoDO info) {
        if (info == null) {
            return null;
        }
        BaseUserInfoDTO user = new BaseUserInfoDTO();
        // todo 知识点，bean属性拷贝的几种方式， 直接get/set方式，使用BeanUtil工具类(spring, cglib, apache, objectMapper)，序列化方式等
        BeanUtils.copyProperties(info, user);
        // 设置用户最新登录地理位置
        user.setRegion(info.getIp().getLatestRegion());
        // 设置用户角色
        user.setRole(RoleEnum.role(info.getUserRole()));
        return user;
    }


    public static SimpleUserInfoDTO toSimpleInfo(UserInfoDO info) {
        return new SimpleUserInfoDTO().setUserId(info.getUserId())
                .setName(info.getUserName())
                .setAvatar(info.getPhoto())
                .setProfile(info.getProfile());
    }

    public static UserStatisticInfoDTO toUserHomeDTO(UserStatisticInfoDTO userHomeDTO, BaseUserInfoDTO baseUserInfoDTO) {
        if (baseUserInfoDTO == null) {
            return null;
        }
        BeanUtils.copyProperties(baseUserInfoDTO, userHomeDTO);
        return userHomeDTO;
    }
}
