package com.wyk.student.exception;


import com.wyk.exception.CustomizeException;
import com.wyk.student.domain.entity.ErrorEntity;
import io.lettuce.core.RedisCommandExecutionException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.data.redis.RedisConnectionFailureException;
import io.lettuce.core.RedisConnectionException;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {MethodArgumentNotValidException.class, BindException.class, ConstraintViolationException.class})
    public ResponseEntity<ErrorEntity<Map<String,Object>>>
    handleValidationException(Exception e, HttpServletRequest request) {
        log.warn("参数校验异常",e);
        Map<String, Object> errors = getError(e);
        ErrorEntity<Map<String, Object>> errorEntity =
                getErrorEntity(HttpStatus.BAD_REQUEST.value(),errors,request);
        return ResponseEntity.badRequest().body(errorEntity);
    }
    @ExceptionHandler(value = CustomizeException.class)
    public ResponseEntity<ErrorEntity<String>>
    handleCustomizeException(CustomizeException e,HttpServletRequest request) {
        ErrorEntity<String> errorEntity = getErrorEntity(e.getCode(), e.getMessage(), request);
        log.warn("业务异常: {}",e.getMessage());
        log.debug("异常堆栈",e);
        return ResponseEntity.status(e.getCode()).body(errorEntity);
    }
    @ExceptionHandler(value = RedisConnectionFailureException.class)
    public ResponseEntity<ErrorEntity<String>>
    handleException(RedisConnectionFailureException e,HttpServletRequest request) {
        log.error("redis连接超时",e);
        ErrorEntity<String> errorEntity = getErrorEntity(HttpStatus
                .UNAUTHORIZED.value(), "redis连接超时", request);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorEntity);
    }
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ErrorEntity<String>>
    handleException(Exception e,HttpServletRequest request) {
        log.error("服务器异常",e);
        ErrorEntity<String> errorEntity = getErrorEntity(HttpStatus
                .INTERNAL_SERVER_ERROR.value(), "服务器异常,请联系管理员"+e, request);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorEntity);
    }

    private <T> ErrorEntity<T> getErrorEntity(int code,T errors,HttpServletRequest request) {
        return ErrorEntity.<T>builder()
                .timestamp(Instant.now())
                .code(code)
                .path(request.getRequestURI())
                .error(errors)
                .build();
    }
    private Map<String,Object> getError(Exception e) {
        Map<String, Object> errors = new HashMap<>();
        if(e instanceof MethodArgumentNotValidException ex) {
            ex.getBindingResult().getFieldErrors()
                    .forEach(error -> errors
                            .put(error.getField(),error.getDefaultMessage()));
        } else if(e instanceof BindException ex) {
            ex.getFieldErrors().forEach(error -> errors.put(error.getField(),error.getDefaultMessage()));

        } else if(e instanceof ConstraintViolationException ex) {
            ex.getConstraintViolations()
                    .forEach(v -> errors.put(v.getPropertyPath().toString(),v.getMessage()));
        }
        return errors;
    }
}
