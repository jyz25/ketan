package com.ketan.service.article.repository.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ketan.api.model.entity.BaseDO;
import com.ketan.api.model.enums.column.ColumnStatusEnum;
import com.ketan.api.model.enums.column.ColumnTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("column_info")
public class ColumnInfoDO extends BaseDO {

    private static final long serialVersionUID = 1920830534262012026L;

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    /**
     * 专栏名
     */
    private String columnName;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * 作者
     */
    private Long userId;

    /**
     * 简介
     */
    private String introduction;

    /**
     * 封面
     */
    private String cover;

    /**
     * 状态
     *
     * @see ColumnStatusEnum#getCode()
     */
    private Integer state;

    /**
     * 排序
     */
    private Integer section;

    /**
     * 上线时间
     */
    private Date publishTime;

    /**
     * 专栏预计的文章数
     */
    private Integer nums;

    /**
     * 专栏类型：免费、登录阅读、收费阅读等
     * @see ColumnTypeEnum#getType()
     */
    private Integer type;

    public Date getFreeStartTime() {
        return freeStartTime;
    }

    public void setFreeStartTime(Date freeStartTime) {
        this.freeStartTime = freeStartTime;
    }

    /**
     * 免费开始时间
     */
    private Date freeStartTime;

    /**
     * 免费结束时间
     */
    private Date freeEndTime;

    public Date getFreeEndTime() {
        return freeEndTime;
    }

    public void setFreeEndTime(Date freeEndTime) {
        this.freeEndTime = freeEndTime;
    }
}
