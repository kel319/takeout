package com.wyk.student.domain.entity;


import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.wyk.student.domain.enums.GenderEnums;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "用户信息实体类")
@TableName("info")
public class InfoEntity implements Serializable  {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "用户信息表主键ID")
    private Long id;
    @Schema(description = "用户性别枚举,0男1女",example = "男性")
    private GenderEnums gender;
    @Schema(description = "用户电子邮箱",example = "111@wyk.com")
    private String email;
    @Schema(description = "用户电话号码",example = "13110213432")
    private String phone;
    @Schema(description = "用户表ID")
    private Integer userId;
    @Schema(description = "创建日期",defaultValue = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime createTime;
    @Schema(description = "更新日期",defaultValue = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;
}
