package com.ketan.service.article.service.impl;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.ketan.api.model.enums.YesOrNoEnum;
import com.ketan.api.model.vo.article.dto.CategoryDTO;
import com.ketan.service.article.conveter.ArticleConveter;
import com.ketan.service.article.repository.dao.CategoryDao;
import com.ketan.service.article.repository.entity.CategoryDO;
import com.ketan.service.article.service.CategoryService;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author Kindow
 * @date 2024/12/29
 */

@Service
public class CategoryServiceImpl implements CategoryService {

    /**
     * 分类数一般不会特别多，所以做一个全量的内存缓存
     * TODO 后期可改为 Guava -> Redis
     */
    private LoadingCache<Long, CategoryDTO> categoryCaches;

    private final CategoryDao categoryDao; // 使用构造器的方式注入

    public CategoryServiceImpl(CategoryDao categoryDao) {
        this.categoryDao = categoryDao;
    }


    // 依赖注入完成后，构造函数执行后执行，用于类的初始化
    @PostConstruct
    public void init() {
        // 过期策略：默认缓存条目不会过期
        // 达到maximumSize时，使用ALU策略清除缓存中不常用值
        // LRU: 优先移除最近最少使用的条目。
        categoryCaches = CacheBuilder.newBuilder().maximumSize(300)
                .build(new CacheLoader<Long, CategoryDTO>() {
                    @Override
                    public CategoryDTO load(Long categoryId) throws Exception {
                        CategoryDO category = categoryDao.getById(categoryId);
                        if (category == null || category.getDeleted() == YesOrNoEnum.YES.getCode()) {
                            // 缓存穿透 非法categoryId
                            return CategoryDTO.EMPTY;
                        }
                        return new CategoryDTO(categoryId,category.getCategoryName(),category.getRank());
                    }
                });
    }


    // 查询类目名
    @Override
    public String queryCategoryName(Long categoryId) {
        // 缓存中有：直接返回对应值 没有：加载后返回对应值
        // 加载后还没有： 则抛出UncheckedExecutionException(运行时异常，不用显示处理)
        return categoryCaches.getUnchecked(categoryId).getCategory();
    }

    // 查询所有的分类
    @Override
    public List<CategoryDTO> loadAllCategories() {
        if (categoryCaches.size() <= 5) {
            refreshCache();
        }
        List<CategoryDTO> list = new ArrayList<>(categoryCaches.asMap().values());
        list.removeIf(s -> s.getCategoryId() <= 0);
        list.sort(Comparator.comparingInt(CategoryDTO::getRank));
        return list;
    }


    // 刷新缓存
    public void refreshCache() {
        List<CategoryDO> list = categoryDao.listAllCategoriesFromDb();
        categoryCaches.invalidateAll(); // 彻底清空缓存 立即执行
        categoryCaches.cleanUp(); // 手动触发清理过期或无效条目,此处没必要执行
        // do(Data Object) -> 数据库表直接对应
        // dto(Data Transfer Object) -> 用于服务层或接口层传递
        list.forEach(c -> categoryCaches.put(c.getId(), ArticleConveter.toDto(c)));
    }
}
