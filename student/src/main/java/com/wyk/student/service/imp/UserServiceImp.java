package com.wyk.student.service.imp;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wyk.exception.CustomizeException;
import com.wyk.student.aop.RedisInterface;
import com.wyk.student.domain.entity.InfoEntity;
import com.wyk.student.domain.entity.UserEntity;
import com.wyk.student.domain.enums.StudentContext;
import com.wyk.student.domain.request.*;
import com.wyk.student.domain.vo.PageVo;
import com.wyk.student.domain.vo.RecordVo;
import com.wyk.student.domain.vo.UserVo;
import com.wyk.student.mapper.InfoMapper;
import com.wyk.student.mapper.UserMapper;
import com.wyk.student.mapstruct.InfoMapStruct;
import com.wyk.student.mapstruct.UserMapStruct;
import com.wyk.student.service.InfoService;
import com.wyk.student.service.UserService;
import com.wyk.student.util.RabbitUtil;
import com.wyk.util.JwtUtil;
import com.wyk.util.ThreadUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImp extends ServiceImpl<UserMapper, UserEntity> implements UserService {

    private static final String DELAY_ROUTE = "delay";
    private static final String FRONT_EXCHANGE = "front.direct";

    private final PasswordEncoder passwordEncoder;
    private final UserMapStruct userMapStruct;
    private final JwtUtil jwtUtil;
    private final RabbitUtil rabbitUtil;
    private final InfoMapper infoMapper;
    private final InfoMapStruct infoMapStruct;
    @Value(value = "${security.jwt.expiration:7200000}")
    private Long expiration;
    @Override
    public void register(AuthRequest request) {
        //判断用户名是否存在并转换
        UserEntity userEntity = Optional.of(request)
                .filter(req -> !baseMapper.existsByUsername(req.getUsername()))
                .map(userMapStruct::authRequestToUserEntity)
                .orElseThrow(() -> new CustomizeException("用户名已存在", HttpStatus.CONFLICT.value()));
        //注册逻辑
        userEntity.setCreateTime(LocalDateTime.now());
        this.save(userEntity);
    }

    @Override
    public RecordVo login(AuthRequest request) {
        UserEntity user = userMatch(request);
        String token = getToken(user);
        String uuid = rabbitUtil.getCorrelationData();
//        rabbitUtil.send(FRONT_EXCHANGE,DELAY_ROUTE,"5000",user,uuid);
        return new RecordVo(token,expiration,user.getUsername());
    }

    @Override
    public void updatePassword(UpdatePasswordRequest request) {
        UserEntity userEntity = Optional.of(request).map(req -> baseMapper.getUserEntityByUser(req.getUsername(), req.getId()))
                .filter(req -> passwordEncoder.matches(request.getOldPassword(), req.getPassword()))
                .orElseThrow(() -> new CustomizeException("用户名或密码错误", HttpStatus.BAD_REQUEST.value()));
        userEntity.setPassword(passwordEncoder.encode(request.getPassword()));
        baseMapper.updateById(userEntity);
    }

    @Override
    public void deleteByUserRequest(UserRequest userRequest) {
        UserEntity userEntity = Optional.of(userRequest)
                .map(req -> baseMapper.getUserEntityByUser(userRequest.getUsername(), userRequest.getId()))
                .filter(user -> passwordEncoder.matches(userRequest.getPassword(), user.getPassword()))
                .orElseThrow(() -> new CustomizeException("用户名或密码错误", HttpStatus.BAD_REQUEST.value()));
        int i = baseMapper.logicDeleteById(List.of(userEntity.getId()));
        if (i > 0) log.warn("用户删除成功,操作人ID: {},被删除ID: {}", ThreadUtil.get(StudentContext.USER_ID),userEntity.getId());
    }

    @Override
    public void deleteById(Long id) {
        Optional.of(id)
                .map(i -> baseMapper.logicDeleteById(List.of(i)))
                .filter(i -> i > 0)
                .orElseThrow(() -> {
                    log.warn("用户删除失败,操作人ID: {},操作ID: {}",ThreadUtil.get(StudentContext.USER_ID),id);
                    return new CustomizeException("用户删除失败",HttpStatus.BAD_REQUEST.value());
                });
    }

    @Override
    public void deleteByIds(List<Long> ids) {
        Optional.of(ids)
                .map(list -> baseMapper.logicDeleteById(list))
                .filter(i -> i > 0)
                .orElseThrow(() -> {
                    log.warn("用户删除失败,操作人ID: {},操作ID列表: {}",ThreadUtil.get(StudentContext.USER_ID),ids);
                    return new CustomizeException("用户删除失败",HttpStatus.BAD_REQUEST.value());
                });
    }

    @Override
    @RedisInterface(value = "userVo", key = "#ids",bloomKey = "#ids")
    public PageVo<UserVo> selectByIds(List<Long> ids, Long pageNum, Long pageSize) {
        Page<UserVo> page = new Page<>(pageNum,pageSize);
        List<UserVo> userVoList = baseMapper.selectUserWithInfo(page,ids);
        if (userVoList.isEmpty()) {
            return PageVo.of(pageNum, pageSize, Collections.emptyList(), 0L);
        }
        return PageVo.of(page.getCurrent(), page.getSize(), userVoList, page.getTotal());
    }

    @Override
    @RedisInterface(value = "userVo", key = "#pageNum+'_'+#pageSize")
    public PageVo<UserVo> selectUserVo(Long pageNum, Long pageSize) {
        Page<UserEntity> page = new Page<>(pageNum,pageSize);
        List<UserVo> userVoList = baseMapper.selectUserWithInfo(page,null);
        return PageVo.<UserVo>builder()
                .pageNum(page.getCurrent())
                .pageSize(page.getSize())
                .total(page.getTotal())
                .records(userVoList)
                .build();
        return PageVo.of(page.getCurrent(),page.getSize(),userVoList,page.getTotal());
    }

    @Override
    public void updateUserInfo(InfoRequest infoRequest) {
        InfoEntity infoEntity = Optional.of(infoRequest).map(infoMapStruct::dtoToEntity)
                .filter(info -> info.getUserId() != null)
                .orElseThrow(() -> new CustomizeException("用户信息格式有误", HttpStatus.BAD_REQUEST.value()));
        infoMapper.updateByUserId(infoEntity);
    }

    @Override
    public PageVo<UserVo> selectByUserInfo(UserInfoRequest request, Long pageNum, Long pageSize) {
        Page<UserVo> page = new Page<>(pageNum, pageSize);
        List<UserVo> userVoList = baseMapper.selectByUserInfo(page,request);
        return PageVo.of(page.getCurrent(),page.getSize(),userVoList,page.getTotal());
    }

    //获取token
    private String getToken(UserEntity user) {
        return jwtUtil.createJwt(user.getId().toString(), Map.of("username", user.getUsername()));
    }
    //密码验证
    private UserEntity userMatch(AuthRequest request) {
        return Optional.of(request)
                .map(req -> baseMapper.getUserEntityByUser(req.getUsername()))
                .filter(user -> passwordEncoder.matches(request.getPassword(),user.getPassword()))
                .orElseThrow(() -> new CustomizeException("用户名或密码错误", HttpStatus.UNAUTHORIZED.value()));
    }
}
