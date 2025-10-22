package com.wyk.student.domain.vo;

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
@Schema(description = "套餐_菜品表Vo")
public class ComboDishVo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Schema(description = "菜品表主键ID")
    private Long id;
    @Schema(description = "菜品名")
    private String name;
    @Schema(description = "菜品价格")
    private BigDecimal price;
    @Schema(description = "菜品剩余库存(-1代表不限量)")
    private Integer inv;
    @Schema(description = "菜品图片")
    private String img;
    @Schema(description = "菜品数量")
    private Integer count;
    @Schema(description = "所属套餐ID")
    private Long comboId;
}
