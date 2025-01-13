package com.ketan.service.rank.service;

import com.ketan.api.model.enums.rank.ActivityRankTimeEnum;
import com.ketan.api.model.vo.rank.dto.RankItemDTO;

import java.util.List;

public interface UserActivityRankService {

    /**
     * 查询活跃度排行榜
     */
    List<RankItemDTO> queryRankList(ActivityRankTimeEnum time, int size);
}
