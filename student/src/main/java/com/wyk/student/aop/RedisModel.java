package com.wyk.student.aop;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RedisModel {
    QUERY("query"),
    UPDATE("update");

    private final String name;
}
