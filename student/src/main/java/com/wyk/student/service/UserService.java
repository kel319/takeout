package com.wyk.student.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wyk.student.domain.entity.UserEntity;
import com.wyk.student.domain.request.*;
import com.wyk.student.domain.vo.PageVo;
import com.wyk.student.domain.vo.RecordVo;
import com.wyk.student.domain.vo.UserVo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public interface UserService extends IService<UserEntity> {
    void register(@Valid AuthRequest request);

    RecordVo login(@Valid AuthRequest request);


    void deleteByUserRequest(@Valid UserRequest userRequest);

    void deleteById(Long id);

    void deleteByIds(@NotNull(message = "批量删除的id集合不能为空") List<Long> ids);

    PageVo<UserVo> selectByIds(@NotNull(message = "批量删除的id集合不能为空") List<Long> ids, Long pageNum, Long pageSize);

    PageVo<UserVo> selectUserVo(Long pageNum, Long pageSize);

    void updatePassword(@Valid UpdatePasswordRequest request);

    void updateUserInfo(@Valid InfoRequest infoRequest);

    PageVo<UserVo> selectByUserInfo(@Valid UserInfoRequest request, Long pageNum, Long pageSize);
}
