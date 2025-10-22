package com.wyk.student.domain.request;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.wyk.student.domain.enums.StatusEnums;
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
@Schema(description = "菜品表Vo")
public class TermDishRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Schema(description = "菜品名")
    private String name;
    @Schema(description = "最大菜品价格")
    private BigDecimal priceMax;
    @Schema(description = "最小菜品价格")
    private BigDecimal priceMin;
    @Schema(description = "菜品剩余库存(-1代表不限量)")
    private Integer inv;
    @Schema(description = "菜品状态,0下架1上架")
    private StatusEnums status;
    @Schema(description = "创建人")
    private Long createBy;
}
