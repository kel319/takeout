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
public enum GoodsEnums implements BaseEnum<Integer> {
    DISH(0,"菜品"),
    COMBO(1,"套餐");

    @EnumValue
    private final Integer code;
    @JsonValue
    private final String desc;

    @JsonCreator
    public static GoodsEnums from(Object message) {
        return EnumUtil.from(GoodsEnums.class,message);
    }
}
