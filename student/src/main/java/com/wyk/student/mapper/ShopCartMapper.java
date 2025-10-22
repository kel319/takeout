package com.wyk.student.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wyk.student.domain.entity.ShopCartEntity;
import com.wyk.student.domain.vo.GoodsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

@Mapper
public interface ShopCartMapper extends BaseMapper<ShopCartEntity> {

    List<GoodsVo> selectGoodsByIds(@Param("userIds") Set<Long> userIds);
}
