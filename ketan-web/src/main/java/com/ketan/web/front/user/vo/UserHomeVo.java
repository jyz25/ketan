package com.ketan.web.front.user.vo;


import com.ketan.api.model.enums.FollowSelectEnum;
import com.ketan.api.model.vo.PageListVo;
import com.ketan.api.model.vo.article.dto.ArticleDTO;
import com.ketan.api.model.vo.article.dto.TagSelectDTO;
import com.ketan.api.model.vo.user.dto.FollowUserInfoDTO;
import com.ketan.api.model.vo.user.dto.UserStatisticInfoDTO;
import lombok.Data;

import java.util.List;


@Data
public class UserHomeVo {
    private String homeSelectType;
    private List<TagSelectDTO> homeSelectTags;
    /**
     * 关注列表/粉丝列表
     */
    private PageListVo<FollowUserInfoDTO> followList;
    /**
     * @see FollowSelectEnum#getCode()
     */
    private String followSelectType;
    private List<TagSelectDTO> followSelectTags;
    private UserStatisticInfoDTO userHome;

    /**
     * 文章列表
     */
    private PageListVo<ArticleDTO> homeSelectList;
}
