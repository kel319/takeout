package com.wyk.student.cache.imp;

import com.fasterxml.jackson.databind.JavaType;
import com.wyk.student.cache.CacheMissHandler;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class EmptyHandler implements CacheMissHandler {
    @Override
    public Object handle(String key, JavaType type) {
        if (List.class.isAssignableFrom(type.getRawClass())) {
            return Collections.emptyList();
        }
        return null;
    }
}
