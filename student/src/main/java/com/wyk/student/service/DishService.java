package com.wyk.student.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wyk.student.domain.entity.DishEntity;
import com.wyk.student.domain.request.DishRequest;
import com.wyk.student.domain.request.TermDishRequest;
import com.wyk.student.domain.vo.DishVo;
import com.wyk.student.domain.vo.GoodsVo;
import com.wyk.student.domain.vo.PageVo;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public interface DishService extends IService<DishEntity> {
    void saveDish(DishRequest request);

    void deleteById(@NotNull Long id);

    void updateByRequest(DishRequest request);

    PageVo<DishVo> getDish(Long pageNum, Long pageSize);

    PageVo<DishVo> getDishByRequest(TermDishRequest request, Long pageNum, Long pageSize);

    List<DishVo> getDish();

    List<GoodsVo> getDishByComboId(Long comboId);
}
