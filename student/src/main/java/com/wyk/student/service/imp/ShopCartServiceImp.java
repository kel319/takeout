package com.wyk.student.service.imp;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wyk.exception.CustomizeException;
import com.wyk.student.domain.entity.ComboEntity;
import com.wyk.student.domain.entity.DishEntity;
import com.wyk.student.domain.entity.ShopCartEntity;
import com.wyk.student.domain.enums.GoodsEnums;
import com.wyk.student.domain.request.ShopCartRequest;
import com.wyk.student.domain.vo.GoodsVo;
import com.wyk.student.domain.vo.PageVo;
import com.wyk.student.domain.vo.ShopCartVo;
import com.wyk.student.mapper.DishMapper;
import com.wyk.student.mapper.ShopCartMapper;
import com.wyk.student.mapstruct.ShopCartMapStruct;
import com.wyk.student.service.ComboService;
import com.wyk.student.service.DishService;
import com.wyk.student.service.ShopCartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
public class ShopCartServiceImp extends ServiceImpl<ShopCartMapper, ShopCartEntity> implements ShopCartService {

    private final ShopCartMapStruct shopCartMapStruct;
    private final DishService dishService;
    private final ComboService comboService;
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertShopCart(ShopCartRequest request) {
        if (Objects.equals(request.getQuantity(),0)) return;
        ShopCartEntity shopCart = lambdaQuery()
                .eq(ShopCartEntity::getGoodsType, request.getGoodsType())
                .eq(ShopCartEntity::getGoodsId, request.getGoodsId())
                .eq(ShopCartEntity::getUserId,request.getUserId())
                .one();
        ShopCartEntity shopCartEntity = Optional.ofNullable(shopCart)
                .map(shop -> {
                    request.setQuantity(shop.getQuantity() + request.getQuantity());
                    shopCartMapStruct.updateEntity(shop, request);
                    return shop;
                }).orElseGet(() -> shopCartMapStruct.dtoToEntity(request));
        BigDecimal price = shopCartEntity.getGoodsType().equals(GoodsEnums.DISH)?
                dishService.lambdaQuery().select(DishEntity::getPrice)
                .eq(DishEntity::getId, shopCartEntity.getGoodsId()).one().getPrice():
                comboService.lambdaQuery().select(ComboEntity::getPrice)
                .eq(ComboEntity::getId, shopCartEntity.getGoodsId()).one().getPrice();
        shopCartEntity.setPrice(price);
        checkRows(saveOrUpdate(shopCartEntity),"新增购物车失败");
    }

    @Override
    public void deleteShopCart(Long id) {
        checkRows(removeById(id),"删除购物车失败");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateGoods(ShopCartRequest request) {
        ShopCartEntity shopCart = lambdaQuery()
                .eq(ShopCartEntity::getGoodsType, request.getGoodsType())
                .eq(ShopCartEntity::getGoodsId, request.getGoodsId())
                .eq(ShopCartEntity::getUserId,request.getUserId())
                .one();
        if (shopCart != null) {
            if (request.getQuantity() > 0) {
                shopCart.setQuantity(request.getQuantity());
                checkRows(updateById(shopCart), "修改商品失败");
            } else removeById(shopCart.getId());
        } else {
            log.debug("要修改的商品不存在");
        }
    }

    /***
     *
     * @param pageNum 当前页码
     * @param pageSize 每页数量
     * @return 分页格式的商品Vo
     */
    @Override
    @Transactional(readOnly = true)
    public PageVo<ShopCartVo> selectGoods(Long pageNum, Long pageSize) {
        // 分页全表查询用户名去重
        Page<ShopCartEntity> entityPage = lambdaQuery()
                .select(ShopCartEntity::getUserId)
                .groupBy(ShopCartEntity::getUserId)
                .page(new Page<>(pageNum, pageSize));
        // 转换成vo列表
        List<ShopCartVo> shopCarts = entityPage
                .getRecords().stream()
                .map(shopCartMapStruct::entityToVo).toList();

        // 将userid提取出来组合成集合
        Set<Long> userIds = shopCarts.stream()
                .map(ShopCartVo::getUserId)
                .collect(Collectors.toSet());
        // 根据购物车表联合查询菜品和套餐表获取商品id，商品数量和商品总价
        List<GoodsVo> goodsVos = baseMapper.selectGoodsByIds(userIds);
        // 根据userid分类，并将总价求和，得到整个购物车商品的总价
        Map<Long, BigDecimal> price = goodsVos.stream().collect(Collectors.groupingBy(
                GoodsVo::getUserId, Collectors.mapping(
                        GoodsVo::getPrice, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add)
                )));
        // 根据userid分类，得到商品信息
        Map<Long, List<GoodsVo>> goodsMap = goodsVos.stream()
                .collect(Collectors.groupingBy(GoodsVo::getUserId));
        // 根据userid存入商品信息和总价
        shopCarts.forEach(shopCartVo -> {
            shopCartVo.setGoodsList(goodsMap.getOrDefault(shopCartVo.getUserId(),List.of()));
            shopCartVo.setPrices(price.getOrDefault(shopCartVo.getUserId(), BigDecimal.valueOf(0)));
        });
        return PageVo.of(entityPage.getCurrent(),entityPage.getSize(),shopCarts,entityPage.getTotal());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShopCartVo> selectGoods(Long id) {
        List<ShopCartVo> shopCartVos = lambdaQuery().select(ShopCartEntity::getUserId)
                .groupBy(ShopCartEntity::getUserId)
                .eq(ShopCartEntity::getUserId, id).list().stream().map(shopCartMapStruct::entityToVo).toList();
        if (shopCartVos.isEmpty()) return shopCartVos;
        List<GoodsVo> goodsVos = baseMapper.selectGoodsByIds(Set.of(id));
        shopCartVos.forEach(shopCartVo -> shopCartVo.setGoodsList(goodsVos != null ?goodsVos:List.of()));
        return shopCartVos;
    }


    private void checkRows(boolean b,String message) {

        if (!b) throw new CustomizeException(message, HttpStatus.BAD_REQUEST.value());
    }
}
