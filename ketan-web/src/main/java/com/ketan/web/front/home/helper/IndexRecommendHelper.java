package com.ketan.web.front.home.helper;

import com.ketan.api.model.context.ReqInfoContext;
import com.ketan.api.model.enums.ConfigTypeEnum;
import com.ketan.api.model.vo.PageListVo;
import com.ketan.api.model.vo.PageParam;
import com.ketan.api.model.vo.article.dto.ArticleDTO;
import com.ketan.api.model.vo.article.dto.CategoryDTO;
import com.ketan.api.model.vo.banner.dto.ConfigDTO;
import com.ketan.api.model.vo.recommend.CarouseDTO;
import com.ketan.api.model.vo.user.dto.UserStatisticInfoDTO;
import com.ketan.core.async.AsyncUtil;
import com.ketan.core.common.CommonConstants;
import com.ketan.service.article.service.ArticleReadService;
import com.ketan.service.article.service.CategoryService;
import com.ketan.service.config.service.ConfigService;
import com.ketan.service.sidebar.service.SidebarService;
import com.ketan.service.user.service.UserService;
import com.ketan.web.front.home.vo.IndexVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;


@Component
public class IndexRecommendHelper {


    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ArticleReadService articleService;

    @Autowired
    private ConfigService configService;

    @Autowired
    private SidebarService sidebarService;

    @Autowired
    private UserService userService;


    public IndexVo buildIndexVo(String activeTab) {
        IndexVo vo = new IndexVo();
        // 根据activeTab 返回当前被选中的分类信息CategoryDTO
        // 并向vo中注入分类列表categories
        CategoryDTO category = categories(activeTab, vo);
        // 设置被选中的CategoryId
        vo.setCategoryId(category.getCategoryId());
        vo.setCurrentCategory(category.getCategory());
        // 并行调度实例，提高响应性能
        AsyncUtil.concurrentExecutor("首页响应")
                .async(() -> vo.setArticles(articleList(category.getCategoryId())), "文章列表")
                .async(() -> vo.setTopArticles(topArticleList(category)), "置顶文章")
                .async(() -> vo.setHomeCarouselList(homeCarouselList()), "轮播图")
                .async(() -> vo.setSideBarItems(sidebarService.queryHomeSidebarList()), "侧边栏")
                .async(() -> vo.setUser(loginInfo()), "用户信息")
                .allExecuted()
                .prettyPrint();
        return vo;
    }

    private UserStatisticInfoDTO loginInfo() {
        if (ReqInfoContext.getReqInfo() != null && ReqInfoContext.getReqInfo().getUserId() != null) {
            return userService.queryUserInfoWithStatistic(ReqInfoContext.getReqInfo().getUserId());
        }
        return null;
    }


    /**
     * 轮播图
     *
     * @return
     */
    private List<CarouseDTO> homeCarouselList() {
        List<ConfigDTO> configList = configService.getConfigList(ConfigTypeEnum.HOME_PAGE);
        return configList.stream()
                .map(configDTO -> new CarouseDTO()
                        .setName(configDTO.getName())
                        .setImgUrl(configDTO.getBannerUrl())
                        .setActionUrl(configDTO.getJumpUrl()))
                .collect(Collectors.toList());
    }


    /**
     * 文章列表
     */
    private PageListVo<ArticleDTO> articleList(Long categoryId) {
        return articleService.queryArticlesByCategory(categoryId, PageParam.newPageInstance());
    }

    /**
     * 置顶top 文章列表
     */
    private List<ArticleDTO> topArticleList(CategoryDTO category) {
        List<ArticleDTO> topArticles = articleService.queryTopArticlesByCategory(category.getCategoryId() == 0 ? null : category.getCategoryId());
        if (topArticles.size() < PageParam.TOP_PAGE_SIZE) {
            // 当分类下文章数小于置顶数时，为了避免显示问题，直接不展示
            topArticles.clear();
            return topArticles;
        }

        // 查询分类对应的头图列表
        List<String> topPicList = CommonConstants.HOMEPAGE_TOP_PIC_MAP.getOrDefault(category.getCategory(),
                CommonConstants.HOMEPAGE_TOP_PIC_MAP.get(CommonConstants.CATEGORY_ALL));

        // 替换头图，下面做了一个数组越界的保护，避免当topPageSize数量变大，但是默认的cover图没有相应增大导致数组越界异常
        AtomicInteger index = new AtomicInteger(0);
        topArticles.forEach(s -> s.setCover(topPicList.get(index.getAndIncrement() % topPicList.size())));
        return topArticles;
    }


    /**
     * 返回分类列表
     *
     * @param active 选中的分类
     * @param vo     首页需要的值对象
     * @return 返回选中的分类； 当没有匹配时，返回默认的全部分类
     */
    private CategoryDTO categories(String active, IndexVo vo) {
        List<CategoryDTO> allList = categoryService.loadAllCategories();
        // 查询所有分类的对应的文章数 key: categoryId value: count
        Map<Long, Long> articleCnt = articleService.queryArticleCountsByCategory();
        // 过滤掉文章数为0的分类 getOrDefault 不存在则返回默认值
        allList.removeIf(c -> articleCnt.getOrDefault(c.getCategoryId(), 0L) <= 0L);

        // 刷新选中的分类
        AtomicReference<CategoryDTO> selectedArticle = new AtomicReference<>();
        allList.forEach(category -> {
            if (category.getCategory().equalsIgnoreCase(active)) {
                category.setSelected(true);
                selectedArticle.set(category);
            } else {
                category.setSelected(false);
            }
        });

        // 添加默认的全部分类
        allList.add(0, new CategoryDTO(0L, CategoryDTO.DEFAULT_TOTAL_CATEGORY));
        if (selectedArticle.get() == null) {
            selectedArticle.set(allList.get(0));
            allList.get(0).setSelected(true);
        }

        vo.setCategories(allList);
        return selectedArticle.get();
    }
}
