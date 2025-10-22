package com.wyk.student.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
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
public class UpdatePasswordRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Schema(description = "用户表主键ID",example = "1")
    @NotNull(message = "用户ID不能为空")
    private Long id;
    @Schema(description = "用户名",example = "gaohao123")
    @NotNull(message = "用户名不能为空")
    private String username;
    @Schema(description = "用户旧密码")
    @NotNull(message = "用户密码不能为空")
    @Size(min = 6,max = 12,message = "密码长度必须在6-12之间")
    private String oldPassword;
    @Schema(description = "用户新密码")
    @NotNull(message = "用户密码不能为空")
    @Size(min = 6,max = 12,message = "密码长度必须在6-12之间")
    private String password;
}
