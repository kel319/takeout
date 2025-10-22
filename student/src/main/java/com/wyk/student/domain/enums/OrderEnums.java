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
public enum OrderEnums implements BaseEnum<Integer> {
    UNPAID(0,"未支付"),
    PAID(1,"已支付"),
    CANCELLED(2,"取消");

    @EnumValue
    private final Integer code;
    @JsonValue
    private final String desc;

    @JsonCreator
    public static OrderEnums from(Object message) {
        return EnumUtil.from(OrderEnums.class,message);
    }
}
