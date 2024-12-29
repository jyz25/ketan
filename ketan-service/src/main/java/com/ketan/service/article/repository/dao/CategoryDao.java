package com.ketan.service.article.repository.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ketan.api.model.enums.PushStatusEnum;
import com.ketan.api.model.enums.YesOrNoEnum;
import com.ketan.service.article.repository.entity.CategoryDO;
import com.ketan.service.article.repository.mapper.CategoryMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Kindow
 * @date 2024/12/29
 * @description
 */

@Repository
public class CategoryDao extends ServiceImpl<CategoryMapper, CategoryDO> {


    public List<CategoryDO> listAllCategoriesFromDb() {
        return lambdaQuery()
                .eq(CategoryDO::getDeleted, YesOrNoEnum.NO.getCode())
                .eq(CategoryDO::getStatus, PushStatusEnum.ONLINE.getCode())
                .list();
    }
}
