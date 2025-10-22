package com.wyk.student.mapstruct;

import com.wyk.student.domain.entity.InfoEntity;
import com.wyk.student.domain.request.InfoRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class InfoMapStruct {
    @Mapping(target = "createTime",ignore = true)
    @Mapping(target = "updateTime",ignore = true)
    public abstract InfoEntity dtoToEntity(InfoRequest infoRequest);
}
