package com.wyk.student.cache;

import com.fasterxml.jackson.databind.JavaType;

public interface CacheMissHandler {
    Object handle(String key, JavaType type);
}
