package com.wyk.student.mapstruct;


import com.wyk.student.domain.entity.ComboEntity;
import com.wyk.student.domain.request.ComboRequest;
import com.wyk.student.domain.vo.ComboVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public abstract class ComboMapStruct {
    @Mapping(target = "createTime",ignore = true)
    @Mapping(target = "updateTime",ignore = true)
    @Mapping(target = "createBy",ignore = true)
    @Mapping(target = "updateBy",ignore = true)
    public abstract ComboEntity requestToEntity(ComboRequest request);

    @Mapping(target = "comboDishVos",ignore = true)
    public abstract ComboVo entityToVo(ComboEntity combo);
}
