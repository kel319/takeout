package com.wyk.student.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "套餐菜品dto")
public class ComboDish implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Schema(description = "菜品表主键ID")
    @NotNull(message = "菜品ID不能为空")
    private Long dishId;
    @Schema(description = "菜品数量")
    @NotNull(message = "菜品数量不能为空")
    @Min(value = 1,message = "菜品数量至少为1")
    private Integer count;
}
