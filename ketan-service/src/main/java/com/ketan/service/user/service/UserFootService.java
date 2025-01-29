package com.ketan.service.user.service;

import com.ketan.api.model.enums.DocumentTypeEnum;
import com.ketan.api.model.enums.OperateTypeEnum;
import com.ketan.api.model.vo.PageParam;
import com.ketan.api.model.vo.user.dto.SimpleUserInfoDTO;
import com.ketan.service.comment.repository.entity.CommentDO;
import com.ketan.service.user.repository.entity.UserFootDO;

import java.util.List;

public interface UserFootService {

    /**
     * 保存或更新状态信息
     *
     * @param documentType    文档类型：博文 + 评论
     * @param documentId      文档id
     * @param authorId        作者
     * @param userId          操作人
     * @param operateTypeEnum 操作类型：点赞，评论，收藏等
     * @return
     */
    UserFootDO saveOrUpdateUserFoot(DocumentTypeEnum documentType, Long documentId, Long authorId, Long userId, OperateTypeEnum operateTypeEnum);


    /**
     * 查询已读文章列表
     *
     * @param userId
     * @param pageParam
     * @return
     */
    List<Long> queryUserReadArticleList(Long userId, PageParam pageParam);


    /**
     * 查询收藏文章列表
     *
     * @param userId
     * @param pageParam
     * @return
     */
    List<Long> queryUserCollectionArticleList(Long userId, PageParam pageParam);

    /**
     * 查询文章的点赞用户信息
     *
     * @param articleId
     * @return
     */
    List<SimpleUserInfoDTO> queryArticlePraisedUsers(Long articleId);


    /**
     * 查询用户记录，用于判断是否点过赞、是否评论、是否收藏过
     *
     * @param documentId
     * @param type
     * @param userId
     * @return
     */
    UserFootDO queryUserFoot(Long documentId, Integer type, Long userId);


    /**
     * 保存评论足迹
     * 1. 用户文章记录上，设置为已评论
     * 2. 若改评论为回复别人的评论，则针对父评论设置为已评论
     *
     * @param comment             保存评论入参
     * @param articleAuthor       文章作者
     * @param parentCommentAuthor 父评论作者
     */
    void saveCommentFoot(CommentDO comment, Long articleAuthor, Long parentCommentAuthor);


    /**
     * 删除评论足迹
     *
     * @param comment             保存评论入参
     * @param articleAuthor       文章作者
     * @param parentCommentAuthor 父评论作者
     */
    void removeCommentFoot(CommentDO comment, Long articleAuthor, Long parentCommentAuthor);

}
