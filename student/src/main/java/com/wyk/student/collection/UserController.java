package com.wyk.student.collection;


import com.wyk.student.domain.request.*;
import com.wyk.student.domain.vo.PageVo;
import com.wyk.student.domain.vo.RecordVo;
import com.wyk.student.domain.vo.UserVo;
import com.wyk.student.service.UserService;
import com.wyk.util.Request;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/user")
@Tag(name = "用户API")
@Validated
public class UserController {
    private final UserService userService;
    @Operation(summary = "用户注册")
    @PostMapping("/post")
    public Request<Void> registerUser(@Valid @RequestBody AuthRequest request) {
        userService.register(request);
        return Request.ok("注册成功");
    }
    @Operation(summary = "用户登录")
    @PostMapping("/record")
    public Request<RecordVo> login(@Valid @RequestBody AuthRequest request) {
        RecordVo data = userService.login(request);
        return Request.ok("登录成功",data);
    }
    @Operation(summary = "用户信息修改")
    @PostMapping("/info")
    public Request<Void> updateUserInfo(@Valid @RequestBody InfoRequest infoRequest) {
        userService.updateUserInfo(infoRequest);
        return Request.ok("用户信息修改成功");
    }
    @Operation(summary = "用户注销")
    @PostMapping("/delete")
    public Request<Void> deleteUser(@Valid @RequestBody UserRequest userRequest) {
        userService.deleteByUserRequest(userRequest);
        return Request.ok("用户注销成功");
    }
    @Operation(summary = "用户删除")
    @DeleteMapping("/{id}")
    public Request<Void> deleteUser(@PathVariable @Size(min = 1,message = "用户id必须大于0") Long id) {
        userService.deleteById(id);
        return Request.ok("用户删除成功");
    }
    @Operation(summary = "用户批量删除")
    @PostMapping("/list/delete")
    public Request<Void> deleteListUser(@RequestParam @NotNull(message = "批量删除的id集合不能为空") List<Long> ids) {
        userService.deleteByIds(ids);
        return Request.ok("用户批量删除成功,ids = "+ids);
    }
    @Operation(summary = "用户查询")
    @GetMapping("/list")
    public Request<PageVo<UserVo>> selectUsersByIds(@RequestParam @NotEmpty(message = "id集合不能为空") List<Long> ids,
                                               @RequestParam(defaultValue = "1") Long pageNum,
                                               @RequestParam(defaultValue = "10") Long pageSize
    ) {
        PageVo<UserVo> pageVo = userService.selectByIds(ids,pageNum,pageSize);
        return Request.ok(pageVo);
    }
    @Operation(summary = "用户全表查询")
    @GetMapping
    public Request<PageVo<UserVo>> selectUsers(@RequestParam(defaultValue = "1") Long pageNum,
                                               @RequestParam(defaultValue = "10") Long pageSize) {
        PageVo<UserVo> pageVo = userService.selectUserVo(pageNum,pageSize);
        return Request.ok(pageVo);
    }
    @Operation(summary = "用户条件查询")
    @PostMapping
    public Request<PageVo<UserVo>> selectUser(@Valid @RequestBody UserInfoRequest request,
                                                    @RequestParam(defaultValue = "1") Long pageNum,
                                                    @RequestParam(defaultValue = "10") Long pageSize
    ) {
        PageVo<UserVo> pageVo = userService.selectByUserInfo(request,pageNum,pageSize);
        return Request.ok(pageVo);
    }
    @Operation(summary = "修改密码")
    @PutMapping
    public Request<Void> updatePassword(@Valid @RequestBody UpdatePasswordRequest request) {
        userService.updatePassword(request);
        return Request.ok("密码修改成功");
    }

}
