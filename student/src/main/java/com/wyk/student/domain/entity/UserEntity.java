package com.wyk.student.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.wyk.student.domain.enums.DeleteEnums;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.annotations.Delete;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Schema(description = "用户实体类")
@TableName(value = "user")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Schema(description = "用户表主键ID",example = "1")
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;
    @Schema(description = "用户名",example = "gaohao123") private String username;
    @Schema(description = "用户密码") private String password;
    @Schema(description = "姓名") private String name;
    @Schema(description = "创建日期")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "修改日期")
    private LocalDateTime updateTime;
    @Schema(description = "逻辑删除的值: 0: 未删除  1: 删除")
    private DeleteEnums deleted;
}
