package com.wyk.student.domain.request;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wyk.student.domain.entity.OrderEntity;
import com.wyk.student.domain.enums.OrderEnums;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderQueryRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Schema(description = "用户ID")
    @NotNull(message = "用户ID不能为空")
    private Long userId;
    @Schema(description = "订单号")
    private Long orderId;
    @Schema(description = "订单状态")
    private OrderEnums orderStatus;

    public static LambdaQueryWrapper<OrderEntity> getWrapper(OrderQueryRequest request) {
        return new LambdaQueryWrapper<OrderEntity>()
                .eq(request.getOrderId() != null,OrderEntity::getUserId,request.getUserId())
                .eq(request.getOrderId() != null,OrderEntity::getOrderId,request.getOrderId())
                .eq(request.getOrderStatus() != null,OrderEntity::getOrderStatus,request.getOrderStatus());
    }
}
