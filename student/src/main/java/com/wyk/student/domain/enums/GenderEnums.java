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
public enum GenderEnums implements BaseEnum<Integer> {
    MALE(0,"男性"),
    FEMALE(1,"女性");

    @EnumValue
    private final Integer code;
    @JsonValue
    private final String desc;

    @JsonCreator
    public static GenderEnums from(Object message) {
        return EnumUtil.from(GenderEnums.class,message);
    }
}
