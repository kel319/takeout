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
public enum DeleteEnums implements BaseEnum<Integer> {
    UNDELETE(0,"未删除"),
    DELETE(1,"已删除");

    @EnumValue
    private final Integer code;
    @JsonValue
    private final String desc;

    @JsonCreator
    public static DeleteEnums from(Object message) {
        return EnumUtil.from(DeleteEnums.class,message);
    }
}
