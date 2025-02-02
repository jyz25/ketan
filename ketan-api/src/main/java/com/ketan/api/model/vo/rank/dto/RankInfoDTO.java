package com.ketan.api.model.vo.rank.dto;

import com.ketan.api.model.enums.rank.ActivityRankTimeEnum;
import lombok.Data;

import java.util.List;

/**
 * 排行榜信息
 */
@Data
public class RankInfoDTO {
    private ActivityRankTimeEnum time;
    private List<RankItemDTO> items;
}
