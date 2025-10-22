package com.wyk.student.collection;


import com.wyk.student.domain.request.ShopCartRequest;
import com.wyk.student.domain.vo.PageVo;
import com.wyk.student.domain.vo.ShopCartVo;
import com.wyk.student.service.ShopCartService;
import com.wyk.util.Request;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shop")
@Tag(name = "购物车管理")
@RequiredArgsConstructor
@Validated
public class ShopCartController {

    private final ShopCartService shopCartService;

    @Operation(summary = "新增购物车")
    @PostMapping
    public Request<Void> insertShopCart(@Valid @RequestBody ShopCartRequest request) {
        shopCartService.insertShopCart(request);
        return Request.ok("购物车添加成功");
    }
    @Operation(summary = "删除购物车")
    @DeleteMapping("/{id}")
    public Request<Void> deleteShopCart(@NotNull(message = "被删除得资源id不能为null")
                                        @Min(value = 1,message = "被删除的资源id不合法")
                                        @PathVariable Long id) {
        shopCartService.deleteShopCart(id);
        return Request.ok("购物车新增商品添加成功");
    }
    @Operation(summary = "修改商品")
    @PutMapping
    public Request<Void> updateShopCart(@Valid @RequestBody ShopCartRequest request) {
        shopCartService.updateGoods(request);
        return Request.ok("商品更改成功");
    }
    @Operation(summary = "查询购物车")
    @GetMapping
    public Request<PageVo<ShopCartVo>> selectShopCart(@RequestParam(defaultValue = "1",required = false) Long pageNum,
                                                      @RequestParam(defaultValue = "10",required = false) Long pageSize
    ) {
        PageVo<ShopCartVo> shopCartVoPageVo = shopCartService.selectGoods(pageNum,pageSize);
        return Request.ok("购物车查询成功",shopCartVoPageVo);
    }
    @Operation(summary = "查询单个购物车")
    @GetMapping("/{id}")
    public Request<List<ShopCartVo>> selectShopCart(@NotNull(message = "被删除得资源id不能为null")
                                                      @Min(value = 1,message = "被删除的资源id不合法")
                                                      @PathVariable Long id) {
        List<ShopCartVo> shopCartVoPageVo = shopCartService.selectGoods(id);
        return Request.ok("购物车查询成功",shopCartVoPageVo);
    }
}
