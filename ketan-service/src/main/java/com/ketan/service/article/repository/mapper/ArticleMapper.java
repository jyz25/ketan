package com.ketan.service.article.repository.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ketan.api.model.vo.PageParam;
import com.ketan.api.model.vo.article.dto.SimpleArticleDTO;
import com.ketan.api.model.vo.article.dto.YearArticleDTO;
import com.ketan.service.article.repository.entity.ArticleDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

// 文章Mapper接口
public interface ArticleMapper extends BaseMapper<ArticleDO> {

    /**
     * 根据阅读次数获取热门文章
     *
     * @param pageParam
     * @return
     */
    List<SimpleArticleDTO> listArticlesByReadCounts(@Param("pageParam") PageParam pageParam);


    /**
     * 根据用户ID获取创作历程
     *
     * @param userId
     * @return
     */
    List<YearArticleDTO> listYearArticleByUserId(@Param("userId") Long userId);

}
