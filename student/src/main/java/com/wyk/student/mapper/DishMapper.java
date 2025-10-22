package com.wyk.student.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wyk.student.domain.entity.DishEntity;
import com.wyk.student.domain.request.TermDishRequest;
import com.wyk.student.domain.vo.DishVo;
import com.wyk.student.domain.vo.GoodsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DishMapper extends BaseMapper<DishEntity> {
    void updateDishById(@Param("dish") DishEntity dish);

    int insertByDishEntity(@Param("dish") DishEntity dish);

    List<DishVo> selectDishVoByRequest(Page<DishVo> page, @Param("request") TermDishRequest request);

    List<GoodsVo> selectDishByComboId(@Param("comboId") Long comboId);

    boolean invDeductByKeysAndArgs(@Param("keys") List<Long> keys, @Param("args") List<Integer> args);
}
