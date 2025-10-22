package com.wyk.student.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wyk.student.domain.entity.ComboEntity;
import com.wyk.student.domain.request.ComboRequest;
import com.wyk.student.domain.vo.ComboDishVo;
import com.wyk.student.domain.vo.ComboVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ComboMapper extends BaseMapper<ComboEntity> {
    int updateCombo(@Param("comboEntity") ComboEntity comboEntity);

    int insertComboAndDish(@Param("request") ComboRequest request);

    int insertCombo(@Param("comboEntity") ComboEntity comboEntity);

    int deleteComboDishByComboId(@Param("comboId") Long comboId);

    List<ComboDishVo> selectComboDishByComboId(@Param("comboIds") List<Long> comboId);
}
