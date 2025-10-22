package com.wyk.student.domain.request;

import com.wyk.student.domain.enums.GoodsEnums;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "购物车dto类")
public class ShopCartRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    @Schema(description = "用户表ID")
    @Min(value = 1,message = "用户ID不合法")
    @NotNull(message = "用户ID不能为空")
    private Long userId;
    @Schema(description = "商品分类,默认菜单")
    private GoodsEnums goodsType = GoodsEnums.DISH;
    @Schema(description = "商品ID")
    @NotNull(message = "商品ID不能为空")
    private Long goodsId;
    @NotNull(message = "商品数量不能为空")
    @Schema(description = "商品数量")
    private Integer quantity;
}
