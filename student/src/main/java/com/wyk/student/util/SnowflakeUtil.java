package com.wyk.student.util;


import com.relops.snowflake.Snowflake;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SnowflakeUtil {

    private final Snowflake snowflake;

    public SnowflakeUtil(@Value("${snowflake.node-id}") int nodeId) {
        this.snowflake = new Snowflake(nodeId);
    }

    public long nextId() {
        return snowflake.next();
    }
}
