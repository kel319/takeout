package com.wyk.student.domain.request;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wyk.student.domain.entity.ComboEntity;
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
@Schema(description = "套餐表查询Dto")
public class ComboQueryRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Schema(description = "套餐名")
    private String name;
    @Schema(description = "套餐最大价格")
    private BigDecimal priceMax;
    @Schema(description = "套餐最小价格")
    private BigDecimal priceMin;
    @Schema(description = "创建人")
    private Long createBy;
    @Schema(description = "套餐状态")
    private StatusEnums status;

    public static LambdaQueryWrapper<ComboEntity> getWrapper(ComboQueryRequest request) {
        return new LambdaQueryWrapper<ComboEntity>()
                .likeRight(request.getName() != null && !request.getName().trim().isEmpty(), ComboEntity::getName, request.getName())
                .eq(request.getStatus() != null, ComboEntity::getStatus, request.getStatus())
                .eq(request.getCreateBy() != null, ComboEntity::getCreateBy, request.getCreateBy())
                .between(request.getPriceMin() != null && request.getPriceMax() != null,
                        ComboEntity::getPrice, request.getPriceMin(), request.getPriceMax())
                .le(request.getPriceMin() == null && request.getPriceMax() != null, ComboEntity::getPrice, request.getPriceMax())
                .ge(request.getPriceMin() != null && request.getPriceMax() == null, ComboEntity::getPrice, request.getPriceMin());
    }
}
