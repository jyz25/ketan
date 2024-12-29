package com.ketan.web.front.home.helper;

import com.ketan.api.model.vo.article.dto.CategoryDTO;
import com.ketan.service.article.service.ArticleReadService;
import com.ketan.service.article.service.CategoryService;
import com.ketan.web.front.home.vo.IndexVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @auther Kindow
 * @date 2024/12/29
 * @project ketan
 * @description 首页推荐相关
 */

@Component
public class IndexRecommendHelper {


    @Autowired
    private CategoryService categoryService;


    @Autowired
    private ArticleReadService articleService;


    public IndexVo buildIndexVo(String activeTab) {
        IndexVo vo = new IndexVo();
        // 根据activeTab 返回当前被选中的分类信息CategoryDTO
        CategoryDTO category = categories(activeTab, vo);

    }


    /**
     * 返回分类列表
     *
     * @param activeTab 选中的分类
     * @param vo        返回结果
     * @return 返回选中的分类； 当没有匹配时，返回默认的全部分类
     */
    private CategoryDTO categories(String activeTab, IndexVo vo) {
        List<CategoryDTO> allList = categoryService.loadAllCategories();
        // 查询所有分类的对应的文章数
        Map<Long, Long> articleCnt = articleService.queryArticleCountsByCategory();
        // 过滤掉文章数为0的分类 getOrDefault 不存在则返回默认值
        allList.removeIf(c -> articleCnt.getOrDefault(c.getCategoryId(), 0L) <= 0L);


    }
}
