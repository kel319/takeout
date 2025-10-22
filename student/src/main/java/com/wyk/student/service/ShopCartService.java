package com.wyk.student.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wyk.student.domain.entity.ShopCartEntity;
import com.wyk.student.domain.request.ShopCartRequest;
import com.wyk.student.domain.vo.PageVo;
import com.wyk.student.domain.vo.ShopCartVo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public interface ShopCartService extends IService<ShopCartEntity> {
    void insertShopCart(@Valid ShopCartRequest request);

    void deleteShopCart(@NotNull(message = "被删除得资源id不能为null") Long id);

    void updateGoods(@Valid ShopCartRequest request);

    PageVo<ShopCartVo> selectGoods(Long pageNum, Long pageSize);

    List<ShopCartVo> selectGoods(@NotNull(message = "查询得资源id不能为null") @Min(value = 1,message = "查询的资源id不合法") Long id);
}
