package com.ketan.service.user.repository.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ketan.api.model.enums.FollowStateEnum;
import com.ketan.service.user.repository.entity.UserRelationDO;
import com.ketan.service.user.repository.mapper.UserRelationMapper;
import org.springframework.stereotype.Repository;

/**
 * 用户相关DB操作
 */
@Repository
public class UserRelationDao extends ServiceImpl<UserRelationMapper, UserRelationDO> {


    /**
     * 获取关注信息
     *
     * @param userId       登录用户
     * @param followUserId 关注的用户
     * @return
     */
    public UserRelationDO getUserRelationByUserId(Long userId, Long followUserId) {
        QueryWrapper<UserRelationDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(UserRelationDO::getUserId, userId)
                .eq(UserRelationDO::getFollowUserId, followUserId)
                .eq(UserRelationDO::getFollowState, FollowStateEnum.FOLLOW.getCode());
        return baseMapper.selectOne(queryWrapper);
    }

}

