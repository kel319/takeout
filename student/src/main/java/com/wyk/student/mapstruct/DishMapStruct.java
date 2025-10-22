package com.wyk.student.mapstruct;


import com.wyk.student.domain.entity.DishEntity;
import com.wyk.student.domain.request.DishRequest;
import com.wyk.student.domain.vo.DishVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public abstract class DishMapStruct {

    @Mapping(target = "img",ignore = true)
    @Mapping(target = "createTime",ignore = true)
    @Mapping(target = "updateTime",ignore = true)
    @Mapping(target = "createBy",ignore = true)
    @Mapping(target = "updateBy",ignore = true)
    public abstract DishEntity requestToEntity(DishRequest dishRequest);
    public abstract DishVo entityToVo(DishEntity dishEntity);
    public abstract List<DishVo> entityToVo(List<DishEntity> dishEntityList);
}
