package com.wyk.student.domain.vo;

import com.wyk.student.domain.enums.GoodsEnums;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "购物车Vo类")
public class GoodsVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "用户ID")
    private Long userId;
    @Schema(description = "商品ID")
    private Long goodsId;
    @Schema(description = "商品分类")
    private GoodsEnums goodsType;
    @Schema(description = "商品名")
    private String goodsName;
    @Schema(description = "商品数量")
    private Integer quantity;
    @Schema(description = "商品总价")
    private BigDecimal price;
}
