package com.ketan.service.sidebar.service;


import com.ketan.api.model.vo.recommend.SideBarDTO;

import java.util.List;

public interface SidebarService {
    /**
     * 查询首页的侧边栏信息
     *
     * @return
     */
    List<SideBarDTO> queryHomeSidebarList();
}
