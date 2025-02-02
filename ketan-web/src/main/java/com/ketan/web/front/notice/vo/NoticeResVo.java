package com.ketan.web.front.notice.vo;


import com.ketan.api.model.vo.PageListVo;
import com.ketan.api.model.vo.notify.dto.NotifyMsgDTO;
import lombok.Data;

import java.util.Map;


@Data
public class NoticeResVo {
    /**
     * 消息通知列表
     */
    private PageListVo<NotifyMsgDTO> list;

    /**
     * 每个分类的未读数量
     */
    private Map<String, Integer> unreadCountMap;

    /**
     * 当前选中的消息类型
     */
    private String selectType;
}
