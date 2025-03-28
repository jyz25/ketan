package com.ketan.service.config.converter;


import com.ketan.api.model.vo.article.dto.DictCommonDTO;
import com.ketan.service.config.repository.entity.DictCommonDO;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Banner转换
 */
public class DictCommonConverter {

    public static List<DictCommonDTO> toDTOS(List<DictCommonDO> records) {
        if (CollectionUtils.isEmpty(records)) {
            return Collections.emptyList();
        }
        return records.stream().map(DictCommonConverter::toDTO).collect(Collectors.toList());
    }

    public static DictCommonDTO toDTO(DictCommonDO dictCommonDO) {
        if (dictCommonDO == null) {
            return null;
        }
        DictCommonDTO dictCommonDTO = new DictCommonDTO();
        dictCommonDTO.setTypeCode(dictCommonDO.getTypeCode());
        dictCommonDTO.setDictCode(dictCommonDO.getDictCode());
        dictCommonDTO.setDictDesc(dictCommonDO.getDictDesc());
        dictCommonDTO.setSortNo(dictCommonDO.getSortNo());
        return dictCommonDTO;
    }
}
