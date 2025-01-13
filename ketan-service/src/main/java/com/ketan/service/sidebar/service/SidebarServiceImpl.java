package com.ketan.service.sidebar.service;

import com.google.common.base.Splitter;
import com.ketan.api.model.enums.ConfigTypeEnum;
import com.ketan.api.model.enums.SidebarStyleEnum;
import com.ketan.api.model.enums.rank.ActivityRankTimeEnum;
import com.ketan.api.model.vo.PageListVo;
import com.ketan.api.model.vo.PageParam;
import com.ketan.api.model.vo.article.dto.SimpleArticleDTO;
import com.ketan.api.model.vo.banner.dto.ConfigDTO;
import com.ketan.api.model.vo.rank.dto.RankItemDTO;
import com.ketan.api.model.vo.recommend.SideBarDTO;
import com.ketan.api.model.vo.recommend.SideBarItemDTO;
import com.ketan.service.article.service.ArticleReadService;
import com.ketan.service.config.service.ConfigService;
import com.ketan.service.rank.service.UserActivityRankService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SidebarServiceImpl implements SidebarService {


    @Autowired
    private ArticleReadService articleReadService;

    @Autowired
    private ConfigService configService;


    /**
     * 使用caffeine本地缓存，来处理侧边栏不怎么变动的消息
     * <p>
     * cacheNames -> 类似缓存前缀的概念
     * key -> SpEL 表达式，可以从传参中获取，来构建缓存的key
     * cacheManager -> 缓存管理器，如果全局只有一个时，可以省略
     *
     * @return
     */
    @Override
    @Cacheable(key = "'homeSidebar'", cacheManager = "caffeineCacheManager", cacheNames = "home")
    public List<SideBarDTO> queryHomeSidebarList() {
        List<SideBarDTO> list = new ArrayList<>();
        list.add(noticeSideBar());
        list.add(columnSideBar());
        list.add(hotArticles());
        SideBarDTO bar = rankList();
        if (bar != null) {
            list.add(bar);
        }
        return list;
    }

    /**
     * 公告信息
     *
     * @return
     */
    private SideBarDTO noticeSideBar() {
        List<ConfigDTO> noticeList = configService.getConfigList(ConfigTypeEnum.NOTICE);
        List<SideBarItemDTO> items = new ArrayList<>(noticeList.size());
        noticeList.forEach(configDTO -> {
            List<Integer> configTags;
            if (StringUtils.isBlank(configDTO.getTags())) {
                configTags = Collections.emptyList();
            } else {
                configTags = Splitter.on(",").splitToStream(configDTO.getTags()).map(s -> Integer.parseInt(s.trim())).collect(Collectors.toList());
            }
            items.add(new SideBarItemDTO()
                    .setName(configDTO.getName())
                    .setTitle(configDTO.getContent())
                    .setUrl(configDTO.getJumpUrl())
                    .setTime(configDTO.getCreateTime().getTime())
                    .setTags(configTags)
            );
        });
        return new SideBarDTO()
                .setTitle("关于枫叶社区")
                // TODO 知识星球的
                .setImg("https://img-baofun.zhhainiao.com/fs/9299f9495e4eafc0c5d5ee84840673de.jpg")
                .setUrl("http://localhost:80/article/detail/1")
                .setItems(items)
                .setStyle(SidebarStyleEnum.NOTICE.getStyle());
    }

    /**
     * 推荐教程的侧边栏 column -> 专栏
     *
     * @return
     */
    private SideBarDTO columnSideBar() {
        List<ConfigDTO> columnList = configService.getConfigList(ConfigTypeEnum.COLUMN);
        List<SideBarItemDTO> items = new ArrayList<>(columnList.size());
        columnList.forEach(configDTO -> {
            SideBarItemDTO item = new SideBarItemDTO();
            item.setName(configDTO.getName());
            item.setTitle(configDTO.getContent());
            item.setUrl(configDTO.getJumpUrl());
            item.setImg(configDTO.getBannerUrl());
            items.add(item);
        });
        return new SideBarDTO().setTitle("精选教程").setItems(items).setStyle(SidebarStyleEnum.COLUMN.getStyle());
    }

    /**
     * 热门文章
     *
     * @return
     */
    private SideBarDTO hotArticles() {
        PageListVo<SimpleArticleDTO> vo = articleReadService.queryHotArticlesForRecommend(PageParam.newPageInstance(1, 8));
        List<SideBarItemDTO> items = vo.getList().stream().map(s -> new SideBarItemDTO().setTitle(s.getTitle()).setUrl("/article/detail/" + s.getId()).setTime(s.getCreateTime().getTime())).collect(Collectors.toList());
        return new SideBarDTO().setTitle("热门文章").setItems(items).setStyle(SidebarStyleEnum.ARTICLES.getStyle());
    }



    @Autowired
    private UserActivityRankService userActivityRankService;

    /**
     * 排行榜
     *
     * @return
     */
    private SideBarDTO rankList() {
        List<RankItemDTO> list = userActivityRankService.queryRankList(ActivityRankTimeEnum.MONTH, 8);
        if (list.isEmpty()) {
            return null;
        }
        SideBarDTO sidebar = new SideBarDTO().setTitle("月度活跃排行榜").setStyle(SidebarStyleEnum.ACTIVITY_RANK.getStyle());
        List<SideBarItemDTO> itemList = new ArrayList<>();
        for (RankItemDTO item : list) {
            SideBarItemDTO sideItem = new SideBarItemDTO().setName(item.getUser().getName())
                    .setUrl(String.valueOf(item.getUser().getUserId()))
                    .setImg(item.getUser().getAvatar())
                    .setTime(item.getScore().longValue());
            itemList.add(sideItem);
        }
        sidebar.setItems(itemList);
        return sidebar;
    }
}
