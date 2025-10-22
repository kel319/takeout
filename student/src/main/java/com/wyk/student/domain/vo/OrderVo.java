package com.wyk.student.domain.vo;

import com.wyk.student.domain.enums.OrderEnums;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "订单表实体类")
public class OrderVo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Schema(description = "用户ID")
    private Long userId;
    @Schema(description = "订单号")
    private Long orderId;
    @Schema(description = "订单状态")
    private OrderEnums orderStatus;
    @Schema(description = "支付时间")
    private LocalDateTime payTime;
}
