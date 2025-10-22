package com.wyk.student.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wyk.student.domain.entity.InfoEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface InfoMapper extends BaseMapper<InfoEntity> {
    void updateByUserId(@Param("infoEntity") InfoEntity infoEntity);
}
