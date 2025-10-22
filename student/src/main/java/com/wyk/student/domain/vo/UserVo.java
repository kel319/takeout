package com.wyk.student.domain.vo;

import com.wyk.student.domain.enums.GenderEnums;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserVo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "用户名") private String username;
    @Schema(description = "姓名") private String name;
    @Schema(description = "用户信息表主键ID")
    @NotNull(message = "用户ID不能为空")
    private Long id;
    @Schema(description = "用户性别枚举,0男1女",example = "0")
    private GenderEnums gender;
    @Schema(description = "用户电子邮箱",example = "111@wyk.com")
    private String email;
    @Schema(description = "用户电话号码",example = "13110213432")
    private String phone;
}
