package com.wyk.student.mapstruct;

import com.wyk.student.domain.entity.UserEntity;
import com.wyk.student.domain.request.AuthRequest;
import com.wyk.student.domain.request.UserRequest;
import com.wyk.student.domain.vo.UserVo;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class UserMapStruct {
    @Autowired
    protected PasswordEncoder passwordEncoder;
    @Mapping(target = "id",ignore = true)
    @Mapping(target = "createTime",ignore = true)
    @Mapping(target = "updateTime",ignore = true)
    @Mapping(target = "password",ignore = true)
    @Mapping(target = "deleted",ignore = true)
    public abstract UserEntity authRequestToUserEntity(AuthRequest authRequest);
    @Mapping(target = "createTime",ignore = true)
    @Mapping(target = "updateTime",ignore = true)
    @Mapping(target = "deleted",ignore = true)
    public abstract UserEntity userRequestToUserEntity(UserRequest userRequest);
    public abstract UserVo userEntityToUserVo(UserEntity user);
    public abstract AuthRequest userEntityToAuthRequest(UserEntity userEntity);
    public abstract List<UserVo> userEntitiesToUserVos(List<UserEntity> userEntities);
    public abstract List<UserEntity> authRequestsToUserEntities(List<AuthRequest> authRequests);
    public abstract List<AuthRequest> userEntitiesToAuthRequests(List<UserEntity> userEntities);


    @AfterMapping
    protected void afterUserRequestToUserEntity(UserRequest userRequest, @MappingTarget UserEntity.UserEntityBuilder userBuilder) {
        userBuilder.updateTime(LocalDateTime.now());
        userBuilder.password(passwordEncoder.encode(userRequest.getPassword()));
    }
    @AfterMapping
    protected void afterAuthRequestToUserEntity(AuthRequest authRequest, @MappingTarget UserEntity.UserEntityBuilder userBuilder) {
        userBuilder.updateTime(LocalDateTime.now());
        userBuilder.password(passwordEncoder.encode(authRequest.getPassword()));
    }
}
