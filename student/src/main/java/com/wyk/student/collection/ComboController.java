package com.wyk.student.collection;

import com.wyk.student.domain.request.ComboQueryRequest;
import com.wyk.student.domain.request.ComboRequest;
import com.wyk.student.domain.vo.ComboVo;
import com.wyk.student.domain.vo.PageVo;
import com.wyk.student.service.ComboService;
import com.wyk.util.Request;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/combo")
@Tag(name = "套餐管理")
@RequiredArgsConstructor
@Validated
public class ComboController {

    private final ComboService comboService;

    @Operation(summary = "新增套餐")
    @PostMapping
    public Request<Void> insertCombo(@Valid @RequestBody ComboRequest request) {
        comboService.insertCombo(request);
        return Request.ok("新增套餐成功");
    }
    @Operation(summary = "删除套餐")
    @DeleteMapping("/{id}")
    public Request<Void> deleteCombo(@NotNull(message = "删除的id不能为空") @PathVariable Long id) {
        comboService.deleteCombo(id);
        return Request.ok("删除套餐成功");
    }
    @Operation(summary = "修改套餐")
    @PutMapping
    public Request<Void> updateCombo(@RequestBody ComboRequest request) {
        comboService.updateCombo(request);
        return Request.ok("修改套餐成功");
    }
    @Operation(summary = "套餐全表查询")
    @GetMapping
    public Request<PageVo<ComboVo>> getCombo(@RequestParam(defaultValue = "1") Long pageNum,
                                             @RequestParam(defaultValue = "1") Long pageSize) {
        PageVo<ComboVo> pageVo = comboService.getCombo(pageNum,pageSize);
        return Request.ok("套餐查询成功",pageVo);
    }
    @Operation(summary = "套餐条件查询")
    @PostMapping("/search")
    public Request<PageVo<ComboVo>> getCombo(@RequestParam(defaultValue = "1") Long pageNum,
                                               @RequestParam(defaultValue = "1") Long pageSize,
                                               @RequestBody ComboQueryRequest request) {
        PageVo<ComboVo> pageVo = comboService.getCombo(pageNum,pageSize,request);
        return Request.ok("套餐条件查询成功",pageVo);
    }
}
