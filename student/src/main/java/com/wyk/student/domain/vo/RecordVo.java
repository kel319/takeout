package com.wyk.student.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "登录返回类")
public class RecordVo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Schema(description = "token")
    private String token;
    @Schema(description = "过期时间",example = "7200000")
    private Long expiration;
    @Schema(description = "用户名",example = "gaohao123")
    private String username;
}
