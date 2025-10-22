package com.wyk.student.domain.entity;


import com.baomidou.mybatisplus.annotation.*;
import com.wyk.student.domain.enums.DeleteEnums;
import com.wyk.student.domain.enums.OrderEnums;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "订单表实体类")
@TableName("`order`")
public class OrderEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;
    @Schema(description = "用户ID")
    private Long userId;
    @Schema(description = "订单号")
    private Long orderId;
    @Schema(description = "订单状态")
    @Builder.Default
    private OrderEnums orderStatus = OrderEnums.UNPAID;
    @Schema(description = "支付时间")
    private LocalDateTime payTime;
    @Schema(description = "创建人")
    @TableField(fill = FieldFill.INSERT)
    private Long createBy;
    @Schema(description = "更新人")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateBy;
    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建日期")
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新日期")
    private LocalDateTime updateTime;
    @Schema(description = "版本号,乐观锁用")
    @Version
    @TableField(fill = FieldFill.INSERT)
    private Long version;
    @Schema(description = "逻辑删除")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private DeleteEnums deleted;


}
