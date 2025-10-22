package com.wyk.student.cache.imp;

import com.fasterxml.jackson.databind.JavaType;
import com.wyk.exception.CustomizeException;
import com.wyk.student.cache.CacheMissHandler;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class ExceptionHandler implements CacheMissHandler {
    @Override
    public Object handle(String key, JavaType type) {
        throw new CustomizeException("查询失败,数据不存在: " + key, HttpStatus.BAD_REQUEST.value());
    }
}
