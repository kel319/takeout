package com.wyk.student.collection;


import com.wyk.student.domain.request.DishRequest;
import com.wyk.student.domain.request.TermDishRequest;
import com.wyk.student.domain.vo.DishVo;
import com.wyk.student.domain.vo.PageVo;
import com.wyk.student.service.DishService;
import com.wyk.util.Request;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/dish")
@RequiredArgsConstructor
@Tag(name = "菜单管理")
public class DishController {

    private final DishService dishService;

    @PostMapping
    @Operation(summary = "新增菜品")
    public Request<Void> addDish(@RequestBody DishRequest request) {
        dishService.saveDish(request);
        return Request.ok("菜品新增成功!");
    }
    @DeleteMapping("/{id}")
    @Operation(summary = "删除菜品")
    public Request<Void> deleteDish(@NotNull(message = "id不能为null") @PathVariable Long id) {
        dishService.deleteById(id);
        return Request.ok("菜品删除成功!");
    }
    @PutMapping
    @Operation(summary = "更新菜品")
    public Request<Void> updateDish(@RequestBody DishRequest request) {
        dishService.updateByRequest(request);
        return Request.ok("菜品信息更新成功!");
    }
    @GetMapping
    @Operation(summary = "菜品全表查询(分页)")
    public Request<PageVo<DishVo>> getDish(@RequestParam(defaultValue = "1",required = false) Long pageNum,
                                           @RequestParam(defaultValue = "10",required = false) Long pageSize) {
        PageVo<DishVo> pageVo = dishService.getDish(pageNum,pageSize);
        return Request.ok("菜品表查询成功",pageVo);
    }
    @GetMapping("/list")
    @Operation(summary = "菜品全表查询")
    public Request<List<DishVo>> getDish() {
        List<DishVo> pageVo = dishService.getDish();
        return Request.ok("菜品表查询成功",pageVo);
    }
    @PostMapping("list")
    @Operation(summary = "菜品条件查询(分页)")
    public Request<PageVo<DishVo>> getDish(@RequestBody TermDishRequest request,
                                           @RequestParam(defaultValue = "1",required = false) Long pageNum,
                                           @RequestParam(defaultValue = "10",required = false) Long pageSize) {
        PageVo<DishVo> pageVo = dishService.getDishByRequest(request,pageNum,pageSize);
        return Request.ok("菜品表条件查询成功",pageVo);
    }

}
