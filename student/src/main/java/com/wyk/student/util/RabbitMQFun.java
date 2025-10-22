package com.wyk.student.util;

@FunctionalInterface
public interface RabbitMQFun<T> {
    void execute(T message);
}
