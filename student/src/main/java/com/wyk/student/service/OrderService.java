package com.wyk.student.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wyk.student.domain.entity.OrderEntity;
import com.wyk.student.domain.request.OrderQueryRequest;
import com.wyk.student.domain.vo.OrderVo;
import com.wyk.student.domain.vo.PageVo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public interface OrderService extends IService<OrderEntity> {
    void insertOrder();

    void orderHandler(Long orderId);

    void deleteOrder(@NotNull(message = "orderId不能为空") Long orderId);

    PageVo<OrderVo> selectOrder(Long pageNum, Long pageSize);

    PageVo<OrderVo> selectAllOrder(Long pageNum, Long pageSize);

    PageVo<OrderVo> selectOrderByRequest(Long pageNum, Long pageSize, OrderQueryRequest request);

    void orderPayment(@NotNull(message = "订单号不能为空") Long orderId);
}
