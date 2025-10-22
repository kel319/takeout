package com.wyk.student.mapstruct;


import com.wyk.student.domain.entity.OrderEntity;
import com.wyk.student.domain.request.OrderQueryRequest;
import com.wyk.student.domain.vo.OrderVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE,
nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public abstract class OrderMapStruct {
    @Mapping(target = "createTime",ignore = true)
    @Mapping(target = "updateTime",ignore = true)
    @Mapping(target = "createBy",ignore = true)
    @Mapping(target = "updateBy",ignore = true)
    @Mapping(target = "id",ignore = true)
    @Mapping(target = "payTime",ignore = true)
    @Mapping(target = "version",ignore = true)
    @Mapping(target = "deleted",ignore = true)
    public abstract OrderEntity dtoToEntity(OrderQueryRequest request);
    public abstract OrderVo entityToVo(OrderEntity order);
}
