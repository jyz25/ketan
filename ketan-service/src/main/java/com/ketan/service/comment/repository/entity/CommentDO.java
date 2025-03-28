package com.ketan.service.comment.repository.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import com.ketan.api.model.entity.BaseDO;
import com.ketan.core.sensitive.ano.SensitiveField;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 评论表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("comment")
public class CommentDO extends BaseDO {

    private static final long serialVersionUID = 1L;

    /**
     * 文章ID
     */
    private Long articleId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 评论内容
     */
    @SensitiveField(bind = "content")
    private String content;

    /**
     * 父评论ID
     */
    private Long parentCommentId;

    /**
     * 顶级评论ID
     */
    private Long topCommentId;

    /**
     * 0未删除 1 已删除
     */
    private Integer deleted;
}
