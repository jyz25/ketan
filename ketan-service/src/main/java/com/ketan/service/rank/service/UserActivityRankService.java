package com.ketan.service.rank.service;

import com.ketan.api.model.enums.rank.ActivityRankTimeEnum;
import com.ketan.api.model.vo.rank.dto.RankItemDTO;
import com.ketan.service.rank.service.model.ActivityScoreBo;

import java.util.List;

public interface UserActivityRankService {

    /**
     * 添加活跃分
     *
     * @param userId
     * @param activityScore
     */
    void addActivityScore(Long userId, ActivityScoreBo activityScore);


    /**
     * 查询活跃度排行榜
     */
    List<RankItemDTO> queryRankList(ActivityRankTimeEnum time, int size);


}
