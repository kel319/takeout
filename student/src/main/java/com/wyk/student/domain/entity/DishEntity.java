package com.wyk.student.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
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
@Schema(description = "菜品实体")
@TableName(value = "dish")
public class DishEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Schema(description = "菜品表主键ID")
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;
    @Schema(description = "菜品名")
    private String name;
    @Schema(description = "菜品价格")
    private BigDecimal price;
    @Schema(description = "菜品剩余库存(-1代表不限量)")
    private Integer inv;
    @Schema(description = "菜品图片")
    private String img;
    @Schema(description = "创建人")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long createBy;
    @Schema(description = "更新人")
    @TableField(fill = FieldFill.UPDATE)
    private Long updateBy;
    @Schema(description = "菜品信息创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @Schema(description = "菜品信息更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @Schema(description = "菜品状态,0下架1上架")
    private StatusEnums status;

}
