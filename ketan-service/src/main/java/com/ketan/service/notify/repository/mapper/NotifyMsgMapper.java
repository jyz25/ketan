package com.ketan.service.notify.repository.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ketan.api.model.vo.PageParam;
import com.ketan.api.model.vo.notify.dto.NotifyMsgDTO;
import com.ketan.service.notify.repository.entity.NotifyMsgDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface NotifyMsgMapper extends BaseMapper<NotifyMsgDO> {

    /**
     * 查询文章相关的通知列表
     *
     * @param userId
     * @param type
     * @param page   分页
     * @return
     */
    List<NotifyMsgDTO> listArticleRelatedNotices(@Param("userId") long userId, @Param("type") int type, @Param("pageParam") PageParam page);

    /**
     * 查询关注、系统等没有关联id的通知列表
     *
     * @param userId
     * @param type
     * @param page   分页
     * @return
     */
    List<NotifyMsgDTO> listNormalNotices(@Param("userId") long userId, @Param("type") int type, @Param("pageParam") PageParam page);

    /**
     * 标记消息为已阅读
     *
     * @param ids
     */
    void updateNoticeRead(@Param("ids") List<Long> ids);
}
