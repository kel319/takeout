package com.wyk.student.domain.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.wyk.student.util.BaseEnum;
import com.wyk.student.util.EnumUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;



@Getter
@RequiredArgsConstructor
public enum StatusEnums implements BaseEnum<Integer> {
    STOP(0,"下架"),
    ENABLE(1,"上架");

    @EnumValue
    private final Integer code;
    @JsonValue
    private final String desc;

    @JsonCreator
    public static StatusEnums from(Object message) {
        return EnumUtil.from(StatusEnums.class,message);
    }
}
