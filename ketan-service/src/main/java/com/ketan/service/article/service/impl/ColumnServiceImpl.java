package com.ketan.service.article.service.impl;

import com.ketan.api.model.exception.ExceptionUtil;
import com.ketan.api.model.vo.PageListVo;
import com.ketan.api.model.vo.PageParam;
import com.ketan.api.model.vo.article.dto.ColumnDTO;
import com.ketan.api.model.vo.article.dto.SimpleArticleDTO;
import com.ketan.api.model.vo.constants.StatusEnum;
import com.ketan.api.model.vo.user.dto.BaseUserInfoDTO;
import com.ketan.api.model.vo.user.dto.ColumnFootCountDTO;
import com.ketan.service.article.conveter.ColumnConvert;
import com.ketan.service.article.repository.dao.ColumnArticleDao;
import com.ketan.service.article.repository.dao.ColumnDao;
import com.ketan.service.article.repository.entity.ColumnArticleDO;
import com.ketan.service.article.repository.entity.ColumnInfoDO;
import com.ketan.service.article.service.ColumnService;
import com.ketan.service.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ColumnServiceImpl implements ColumnService {

    @Autowired
    private ColumnArticleDao columnArticleDao;

    @Autowired
    private ColumnDao columnDao;


    @Autowired
    private UserService userService;


    public List<SimpleArticleDTO> queryColumnArticles(long columnId) {
        return columnDao.listColumnArticles(columnId);
    }


    /**
     * 专栏列表
     *
     * @return
     */
    @Override
    public PageListVo<ColumnDTO> listColumn(PageParam pageParam) {
        List<ColumnInfoDO> columnList = columnDao.listOnlineColumns(pageParam);
        List<ColumnDTO> result = columnList.stream().map(this::buildColumnInfo).collect(Collectors.toList());
        return PageListVo.newVo(result, pageParam.getPageSize());
    }

    @Override
    public ColumnDTO queryColumnInfo(Long columnId) {
        return buildColumnInfo(queryBasicColumnInfo(columnId));
    }

    @Override
    public ColumnArticleDO queryColumnArticle(long columnId, Integer section) {
        ColumnArticleDO article = columnDao.getColumnArticleId(columnId, section);
        if (article == null) {
            throw ExceptionUtil.of(StatusEnum.ARTICLE_NOT_EXISTS, section);
        }
        return article;
    }


    @Override
    public ColumnDTO queryBasicColumnInfo(Long columnId) {
        // 查找专栏信息
        ColumnInfoDO column = columnDao.getById(columnId);
        if (column == null) {
            throw ExceptionUtil.of(StatusEnum.COLUMN_NOT_EXISTS, columnId);
        }
        return ColumnConvert.toDto(column);
    }

    private ColumnDTO buildColumnInfo(ColumnInfoDO info) {
        return buildColumnInfo(ColumnConvert.toDto(info));
    }

    /**
     * 构建专栏详情信息
     *
     * @param dto
     * @return
     */
    private ColumnDTO buildColumnInfo(ColumnDTO dto) {
        // 补齐专栏对应的用户信息
        BaseUserInfoDTO user = userService.queryBasicUserInfo(dto.getAuthor());
        dto.setAuthorName(user.getUserName());
        dto.setAuthorAvatar(user.getPhoto());
        dto.setAuthorProfile(user.getProfile());

        // 统计计数
        ColumnFootCountDTO countDTO = new ColumnFootCountDTO();
        // 更新文章数
        countDTO.setArticleCount(columnDao.countColumnArticles(dto.getColumnId()));
        // 专栏阅读人数
        countDTO.setReadCount(columnDao.countColumnReadPeoples(dto.getColumnId()));
        // 总的章节数
        countDTO.setTotalNums(dto.getNums());
        dto.setCount(countDTO);
        return dto;
    }
    @Override
    public ColumnArticleDO getColumnArticleRelation(Long articleId) {
        return columnArticleDao.selectColumnArticleByArticleId(articleId);
    }

}
