package com.wyk.student.mapstruct;


import com.wyk.student.domain.entity.ShopCartEntity;
import com.wyk.student.domain.request.ShopCartRequest;
import com.wyk.student.domain.vo.ShopCartVo;
import org.mapstruct.*;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE,
nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public abstract class ShopCartMapStruct {
    @Mapping(target = "createTime",ignore = true)
    @Mapping(target = "updateTime",ignore = true)
    @Mapping(target = "createBy",ignore = true)
    @Mapping(target = "updateBy",ignore = true)
    @Mapping(target = "id",ignore = true)
    @Mapping(target = "price",ignore = true)
    public abstract void updateEntity(@MappingTarget ShopCartEntity entity, ShopCartRequest request);
    @InheritConfiguration(name = "updateEntity")
    public abstract ShopCartEntity dtoToEntity(ShopCartRequest request);
    public abstract ShopCartVo entityToVo(ShopCartEntity entity);
}
