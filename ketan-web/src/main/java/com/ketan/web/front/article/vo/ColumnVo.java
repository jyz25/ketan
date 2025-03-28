package com.ketan.web.front.article.vo;


import com.ketan.api.model.vo.PageListVo;
import com.ketan.api.model.vo.article.dto.ColumnDTO;
import com.ketan.api.model.vo.recommend.SideBarDTO;
import lombok.Data;

import java.util.List;


@Data
public class ColumnVo {
    /**
     * 专栏列表
     */
    private PageListVo<ColumnDTO> columns;

    /**
     * 侧边栏信息
     */
    private List<SideBarDTO> sideBarItems;

}
