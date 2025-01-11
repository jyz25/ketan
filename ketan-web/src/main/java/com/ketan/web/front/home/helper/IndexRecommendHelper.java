package com.ketan.web.front.home.helper;

import com.ketan.api.model.vo.PageListVo;
import com.ketan.api.model.vo.PageParam;
import com.ketan.api.model.vo.article.dto.ArticleDTO;
import com.ketan.api.model.vo.article.dto.CategoryDTO;
import com.ketan.core.async.AsyncUtil;
import com.ketan.service.article.service.ArticleReadService;
import com.ketan.service.article.service.CategoryService;
import com.ketan.web.front.home.vo.IndexVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;


@Component
public class IndexRecommendHelper {


    @Autowired
    private CategoryService categoryService;


    @Autowired
    private ArticleReadService articleService;


    public IndexVo buildIndexVo(String activeTab) {
        IndexVo vo = new IndexVo();
        // 根据activeTab 返回当前被选中的分类信息CategoryDTO
        // 并向vo中注入分类列表categories
        CategoryDTO category = categories(activeTab, vo);
        // 设置被选中的CategoryId
        vo.setCategoryId(category.getCategoryId());
        // 并行调度实例，提高响应性能 待做
        vo.setArticles(articleList(category.getCategoryId()));

        return null;
    }


    /**
     * 文章列表
     */
    private PageListVo<ArticleDTO> articleList(Long categoryId) {
        return articleService.queryArticlesByCategory(categoryId, PageParam.newPageInstance());
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
