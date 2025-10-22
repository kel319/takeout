package com.wyk.student.util;


import com.wyk.exception.CustomizeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Objects;

@Component
@Slf4j
public class EnumUtil {
    public static <E extends Enum<E> & BaseEnum<T>,T> E from(Class<E> eClass,Object o) {
        if (o == null) return null;
        E[] values = eClass.getEnumConstants();
        if (o instanceof Number code) {
            return Arrays.stream(values).filter(v -> Objects.equals(v.getCode(), code))
                    .findFirst()
                    .orElseThrow(() -> new CustomizeException("无效的枚举值code: " + code, HttpStatus.BAD_REQUEST.value()));
        }
        if (o instanceof String desc) {
            return Arrays.stream(values).filter(v -> Objects.equals(v.getDesc(), desc))
                    .findFirst()
                    .orElseGet(() -> {
                        try {
                            int code = Integer.parseInt(desc);
                            return from(eClass,code);
                        } catch (NumberFormatException e) {
                            throw new CustomizeException("无效的枚举值desc: " + desc, HttpStatus.BAD_REQUEST.value());
                        }
                    });
        }
        throw new CustomizeException("不支持的枚举类型: "+o, HttpStatus.BAD_REQUEST.value());
    }
}
