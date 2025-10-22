package com.wyk.student.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.wyk.student.domain.enums.GoodsEnums;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "购物车实体类")
@TableName("shop_cart")
public class ShopCartEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    @Schema(description = "主键ID")
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;
    @Schema(description = "用户表ID")
    private Long userId;
    @Schema(description = "商品分类")
    private GoodsEnums goodsType;
    @Schema(description = "商品ID")
    private Long goodsId;
    @Schema(description = "商品数量")
    private Integer quantity;
    @Schema(description = "商品总价")
    private BigDecimal price;
    @Schema(description = "创建人")
    @TableField(fill = FieldFill.INSERT)
    private Long createBy;
    @Schema(description = "更新人")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateBy;
    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建日期")
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新日期")
    private LocalDateTime updateTime;
}
