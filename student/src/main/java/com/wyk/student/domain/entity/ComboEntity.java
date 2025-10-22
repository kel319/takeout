package com.wyk.student.domain.entity;


import com.baomidou.mybatisplus.annotation.*;
import com.wyk.student.domain.enums.StatusEnums;
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
@Schema(description = "套餐表实体类")
@TableName("combo")
public class ComboEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @TableId(value = "id",type = IdType.AUTO)
    @Schema(description = "套餐表主键ID")
    private Long id;
    @Schema(description = "套餐名")
    private String name;
    @Schema(description = "套餐价格")
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
    @Schema(description = "套餐状态")
    private StatusEnums status;
}
