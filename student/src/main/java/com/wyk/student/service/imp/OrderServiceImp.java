package com.wyk.student.service.imp;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wyk.exception.CustomizeException;
import com.wyk.student.domain.entity.DishEntity;
import com.wyk.student.domain.entity.ErrorEntity;
import com.wyk.student.domain.entity.OrderEntity;
import com.wyk.student.domain.entity.ShopCartEntity;
import com.wyk.student.domain.enums.DeleteEnums;
import com.wyk.student.domain.enums.GoodsEnums;
import com.wyk.student.domain.enums.OrderEnums;
import com.wyk.student.domain.enums.StudentContext;
import com.wyk.student.domain.request.OrderQueryRequest;
import com.wyk.student.domain.vo.GoodsVo;
import com.wyk.student.domain.vo.OrderVo;
import com.wyk.student.domain.vo.PageVo;
import com.wyk.student.mapper.DishMapper;
import com.wyk.student.mapper.OrderMapper;
import com.wyk.student.mapper.ShopCartMapper;
import com.wyk.student.mapstruct.OrderMapStruct;
import com.wyk.student.service.DishService;
import com.wyk.student.service.OrderService;
import com.wyk.student.util.RabbitUtil;
import com.wyk.student.util.RedisUtil;
import com.wyk.student.util.SnowflakeUtil;
import com.wyk.util.ThreadUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImp extends ServiceImpl<OrderMapper, OrderEntity> implements OrderService {

    private static final String STOCK = "stock";
    private static final String CACHE = "cache";

    private static final String FRONT_EXCHANGE = "front.direct";
    private static final String DELAY_ROUTE = "delay";
//    private static final String TIME_OUT = "1800000";
    private static final String TIME_OUT = "30000";

    private final RabbitUtil rabbitUtil;
    private final SnowflakeUtil snowflakeUtil;
    private final RedisUtil redisUtil;
    private final ShopCartMapper shopCartMapper;
    private final DishService dishService;
    private final DishMapper dishMapper;
    private final OrderMapStruct orderMapStruct;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertOrder() {
        Long userId = ThreadUtil.get(StudentContext.USER_ID);
        Long orderId = snowflakeUtil.nextId();
        ErrorEntity.checkRows(redisUtil.setDistributedLock(userId.toString(),orderId.toString(),10)
                ,"获取分布式锁失败");
        ErrorEntity.checkRows(shopCartMapper.exists(new LambdaQueryWrapper<ShopCartEntity>().eq(ShopCartEntity::getUserId, userId)),
                "用户购物车无商品");
        ErrorEntity.checkRows(!isOrderExists(userId),"用户存在尚未支付的订单,无法重新下单");
        List<GoodsVo> goodsVoList = getAllGoodsVoByUserId(userId);
        List<String> keys = goodsVoList.stream()
                .flatMap(goodsVo -> Stream.of(STOCK + goodsVo.getGoodsId(), CACHE + goodsVo.getGoodsId()))
                .toList();
        List<Integer> args = goodsVoList.stream().map(GoodsVo::getQuantity).toList();
        ErrorEntity.checkRows(redisUtil.deductInv(keys,args),"库存不足");
        log.info("库存扣除成功,keys: {},args: {}",keys,args);
        OrderEntity orderEntity = OrderEntity.builder().userId(userId).orderId(orderId).build();
        try {
            ErrorEntity.checkRows(save(orderEntity),"订单创建失败");
            rabbitUtil.send(FRONT_EXCHANGE,DELAY_ROUTE,TIME_OUT,userId,orderId.toString());
        } catch (Exception e) {
            if (!redisUtil.addInv(keys, args)) log.error("库存回滚失败,userId: {},keys: {}",userId,keys,e);
            log.info("回滚成功");
            throw e;
        } finally {
            ErrorEntity.checkRows(redisUtil.delDistributedLock(userId.toString(),orderId.toString())
                    ,"释放分布式锁失败,可能已被释放");
        }
    }

    @Override
    public void deleteOrder(Long orderId) {
        Long userId = ThreadUtil.get(StudentContext.USER_ID);
        OrderEntity orderEntity = lambdaQuery()
                .select(OrderEntity::getUserId, OrderEntity::getOrderId
                        , OrderEntity::getOrderStatus, OrderEntity::getId, OrderEntity::getDeleted)
                .eq(OrderEntity::getOrderId, orderId)
                .eq(OrderEntity::getUserId, userId)
                .eq(OrderEntity::getDeleted, DeleteEnums.UNDELETE)
                .oneOpt().orElseThrow(() -> new CustomizeException("订单不存在", HttpStatus.BAD_REQUEST.value()));
        orderEntity.setDeleted(DeleteEnums.DELETE);
        ErrorEntity.checkRows(updateById(orderEntity),"删除订单失败");
    }

    @Override
    public PageVo<OrderVo> selectOrder(Long pageNum, Long pageSize) {
        Long userId = ThreadUtil.get(StudentContext.USER_ID);
        Page<OrderEntity> page = lambdaQuery()
                .eq(OrderEntity::getUserId, userId)
                .eq(OrderEntity::getDeleted,DeleteEnums.UNDELETE)
                .page(new Page<>(pageNum, pageSize));
        List<OrderVo> orderVos = page.getRecords().stream()
                .map(orderMapStruct::entityToVo)
                .toList();
        return PageVo.of(page.getCurrent(),page.getSize(),orderVos,page.getTotal());
    }

    @Override
    public PageVo<OrderVo> selectAllOrder(Long pageNum, Long pageSize) {
        Page<OrderEntity> page = lambdaQuery()
                .eq(OrderEntity::getDeleted,DeleteEnums.UNDELETE)
                .page(new Page<>(pageNum, pageSize));
        List<OrderVo> orderVos = page.getRecords().stream()
                .map(orderMapStruct::entityToVo)
                .toList();
        return PageVo.of(page.getCurrent(),page.getSize(),orderVos,page.getTotal());
    }

    @Override
    public PageVo<OrderVo> selectOrderByRequest(Long pageNum, Long pageSize, OrderQueryRequest request) {
        Page<OrderEntity> page = baseMapper.selectPage(new Page<>(pageNum, pageSize), OrderQueryRequest.getWrapper(request));
        List<OrderVo> orderVos = page.getRecords().stream()
                .map(orderMapStruct::entityToVo).toList();
        return PageVo.of(page.getCurrent(),page.getSize(),orderVos,page.getTotal());
    }

    @Override
    public void orderPayment(Long orderId) {
        Long userId = ThreadUtil.get(StudentContext.USER_ID);
        OrderEntity orderEntity = lambdaQuery().eq(OrderEntity::getUserId, userId)
                .eq(OrderEntity::getOrderId, orderId)
                .eq(OrderEntity::getDeleted, DeleteEnums.UNDELETE)
                .eq(OrderEntity::getOrderStatus,OrderEnums.UNPAID)
                .last("limit 1")
                .one();
        if (orderEntity == null) throw new CustomizeException("支付失败,未查到有效订单",HttpStatus.BAD_REQUEST.value());
        BigDecimal prices = shopCartMapper.selectGoodsByIds(Set.of(userId)).stream()
                .map(GoodsVo::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        log.info("消费了{}块钱",prices);
//        支付逻辑相关
        orderEntity.setOrderStatus(OrderEnums.PAID);
        List<GoodsVo> goodsVos = getAllGoodsVoByUserId(userId);
        List<Long> GoodsId = goodsVos.stream().map(GoodsVo::getGoodsId).toList();
        List<String> keys = goodsVos.stream()
                .map(goodsVo -> CACHE+goodsVo.getGoodsId())
                .toList();
        List<Integer> args = goodsVos.stream().map(GoodsVo::getQuantity).toList();
        Map<Long, Integer> newInv = goodsVos.stream().collect(Collectors.toMap(GoodsVo::getGoodsId, GoodsVo::getQuantity));
        Map<Long, Integer> oldInv = dishService.lambdaQuery()
                .in(DishEntity::getId, GoodsId)
                .list()
                .stream()
                .collect(Collectors.toMap(DishEntity::getId, DishEntity::getInv));
        Map<Long, Integer> diff = newInv.keySet().stream()
                .collect(Collectors.toMap(
                        key -> key,
                        key -> {
                            Integer oldValue = oldInv.get(key);
                            Integer newValue = newInv.get(key);
                            if (oldValue == null) {
                                throw new RuntimeException("商品ID " + key + " 在库存中不存在");
                            }
                            if (newValue == null) {
                                throw new RuntimeException("商品ID " + key + " 在请求中不存在");
                            }
                            return oldValue - newValue;
                        }
                ));
        List<Long> dishId = new ArrayList<>(diff.keySet());
        List<Integer> inv = new ArrayList<>(diff.values());
        ErrorEntity.checkRows(redisUtil.paySuccess(keys,args),"锁定库存扣除失败");
        ErrorEntity.checkRows(dishMapper.invDeductByKeysAndArgs(dishId,inv),"数据库扣除库存失败");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void orderHandler(Long userId) {
        OrderEntity orderEntity = lambdaQuery()
                .eq(OrderEntity::getUserId, userId)
                .eq(OrderEntity::getOrderStatus, OrderEnums.UNPAID)
                .last("limit 1")
                .one();
        Optional.ofNullable(orderEntity)
                .filter(order -> order.getOrderStatus().equals(OrderEnums.UNPAID))
                .ifPresent(order -> {
                    order.setOrderStatus(OrderEnums.CANCELLED);
                    ErrorEntity.checkRows(updateById(order),"订单状态修改失败");
                    log.info("成功取消超时订单");
                });
    }





    private boolean isOrderExists(Long userId) {
        return lambdaQuery().select(OrderEntity::getOrderStatus)
                .eq(OrderEntity::getUserId,userId)
                .eq(OrderEntity::getDeleted, DeleteEnums.UNDELETE)
                .eq(OrderEntity::getOrderStatus,OrderEnums.UNPAID)
                .exists();
    }
    private List<GoodsVo> getAllGoodsVoByUserId(Long userId) {
        return shopCartMapper.selectGoodsByIds(Set.of(userId)).stream()
                .flatMap(goodsVo -> {
                    if (goodsVo.getGoodsType().equals(GoodsEnums.COMBO)) {
                        List<GoodsVo> dishByComboId = dishService.getDishByComboId(goodsVo.getGoodsId());
                        return dishByComboId.stream();
                    } else return Stream.of(goodsVo);
                }).collect(Collectors.groupingBy(GoodsVo::getGoodsId, Collectors
                        .reducing((oldGoods, newGoods) -> {
                            oldGoods.setQuantity(oldGoods.getQuantity() + newGoods.getQuantity());
                            return oldGoods;
                        }))).values().stream()
                .map(goodsVo -> goodsVo.orElse(new GoodsVo()))
                .toList();
    }
}
