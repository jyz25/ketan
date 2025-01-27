package com.ketan.service.user.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ketan.api.model.vo.user.dto.SimpleUserInfoDTO;
import com.ketan.service.user.repository.entity.UserFootDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * 用户足迹mapper接口
 */
public interface UserFootMapper extends BaseMapper<UserFootDO> {


    /**
     * 查询文章的点赞列表
     *
     * @param documentId
     * @param type
     * @param size
     * @return
     */
    List<SimpleUserInfoDTO> listSimpleUserInfosByArticleId(@Param("documentId") Long documentId,
                                                           @Param("type") Integer type,
                                                           @Param("size") int size);


}
