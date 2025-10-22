package com.wyk.student.domain.request;

import com.wyk.student.domain.enums.StatusEnums;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
@Schema(description = "套餐表Dto")
public class ComboRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Schema(description = "套餐表主键ID")
    private Long id;
    @Schema(description = "套餐名")
    @NotNull(message = "套餐名不能为空")
    private String name;
    @Schema(description = "套餐价格")
    @NotNull(message = "套餐价格不能为空")
    private BigDecimal price;
    @Valid
    @Schema(description = "套餐内菜品集合")
    private List<ComboDish> comboDishes;
    @Schema(description = "套餐状态")
    private StatusEnums status = StatusEnums.STOP;
}
