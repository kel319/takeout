package com.wyk.student.domain.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wyk.student.domain.entity.ShopCartEntity;
import com.wyk.student.domain.enums.GoodsEnums;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "购物车Vo类")
public class ShopCartVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    @Schema(description = "用户表ID")
    private Long userId;
    @Schema(description = "商品列表")
    private List<GoodsVo> GoodsList;
    @Schema(description = "总价格")
    private BigDecimal prices;

}
