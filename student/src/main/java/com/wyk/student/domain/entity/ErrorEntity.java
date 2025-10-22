package com.wyk.student.domain.entity;

import com.wyk.exception.CustomizeException;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

@Data
@Schema(description = "错误实体")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorEntity<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Schema(description = "时间戳") private Instant timestamp;
    @Schema(description = "状态码") private Integer code;
    @Schema(description = "错误信息") private T error;
    @Schema(description = "路径") private String path;

    public static void checkRows(boolean b,String message) {
        if (!b) throw new CustomizeException(message, HttpStatus.BAD_REQUEST.value());
    }
    public static void checkRows(Integer i,String message) {
        if (i < 1) throw new CustomizeException(message, HttpStatus.BAD_REQUEST.value());
    }
}
