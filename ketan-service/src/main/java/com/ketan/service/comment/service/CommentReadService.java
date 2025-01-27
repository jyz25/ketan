package com.ketan.service.comment.service;

import com.ketan.api.model.vo.PageParam;
import com.ketan.api.model.vo.comment.dto.SubCommentDTO;
import com.ketan.api.model.vo.comment.dto.TopCommentDTO;
import com.ketan.service.comment.converter.CommentConverter;
import com.ketan.service.comment.repository.entity.CommentDO;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public interface CommentReadService {

    /**
     * 查询文章评论列表
     *
     * @param articleId
     * @param page
     * @return
     */
    List<TopCommentDTO> getArticleComments(Long articleId, PageParam page);


    /**
     * 查询热门评论
     *
     * @param articleId
     * @return
     */
    TopCommentDTO queryHotComment(Long articleId);


    /**
     * 根据评论id查询评论信息
     *
     * @param commentId
     * @return
     */
    CommentDO queryComment(Long commentId);

}
