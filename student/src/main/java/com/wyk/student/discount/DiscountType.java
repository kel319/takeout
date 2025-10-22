package com.wyk.student.discount;

import lombok.Getter;

@Getter
public enum DiscountType {
    BEFORE("预处理"),
    MAIN("主处理"),
    AFTER("最终处理");


    private final String name;

    DiscountType(String name) {
        this.name = name;
    }

}
