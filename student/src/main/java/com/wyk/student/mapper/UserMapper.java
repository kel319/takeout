package com.wyk.student.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wyk.student.domain.entity.UserEntity;
import com.wyk.student.domain.request.UserInfoRequest;
import com.wyk.student.domain.request.UserRequest;
import com.wyk.student.domain.vo.UserVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Objects;

@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {

    default UserEntity getUserEntityByUser(String username,Long id) {
        return selectOne(getWrapper(username,id)
                .last("LIMIT 1"));
    }
    default UserEntity getUserEntityByUser(String username) {
        return selectOne(getWrapper(username)
                .last("LIMIT 1"));
    }


    default boolean existsByUsernameAndId(String username,Long id) {
        return exists(getWrapper(username,id));
    }
    default boolean existsByUsername(String username) {
        return exists(new LambdaQueryWrapper<UserEntity>()
                .eq(UserEntity::getUsername,username));
    }
    int logicDeleteById(@Param("ids") List<Long> ids);
    default LambdaQueryWrapper<UserEntity> getWrapper(String username, Long id) {
        return new LambdaQueryWrapper<UserEntity>()
                .eq(UserEntity::getUsername,username)
                .eq(UserEntity::getId,id);
    }
    default LambdaQueryWrapper<UserEntity> getWrapper(String username) {
        return new LambdaQueryWrapper<UserEntity>()
                .eq(UserEntity::getUsername,username);
    }

    List<UserVo> selectUserWithInfo(Page<?> page, @Param("ids") List<Long> ids);

    List<UserVo> selectByUserInfo(Page<UserVo> page, @Param("request")UserInfoRequest request);
}
