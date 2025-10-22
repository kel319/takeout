package com.wyk.student.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.wyk.student.domain.enums.DeleteEnums;
import com.wyk.student.domain.enums.StudentContext;
import com.wyk.util.ThreadUtil;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class AppMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        LocalDateTime now = LocalDateTime.now();
        strictInsertFill(metaObject,"createTime", LocalDateTime.class, now);
        strictInsertFill(metaObject,"updateTime", LocalDateTime.class, now);
        Long id = getUserId();
        if (metaObject.hasSetter("createBy"))
            strictInsertFill(metaObject,"createBy", Long.class, id);
        if (metaObject.hasSetter("updateBy"))
            strictInsertFill(metaObject,"updateBy", Long.class, id);
        if (metaObject.hasSetter("deleted"))
            strictInsertFill(metaObject,"deleted", DeleteEnums.class,DeleteEnums.UNDELETE);
        if (metaObject.hasSetter("version")) {
            strictInsertFill(metaObject, "version", Long.class, 0L);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        LocalDateTime now = LocalDateTime.now();
        strictUpdateFill(metaObject,"updateTime", LocalDateTime.class, now);
        if (metaObject.hasSetter("updateBy"))
            strictUpdateFill(metaObject,"updateBy", Long.class, getUserId());
    }
    private Long getUserId() {
        Object object = ThreadUtil.get(StudentContext.USER_ID);
        if (object == null) return null;
        if (object instanceof Long l) return l;
        if (object instanceof Integer i) return i.longValue();
        try {
            return Long.parseLong(object.toString());
        } catch (Exception e) {
            return null;
        }
    }
}
