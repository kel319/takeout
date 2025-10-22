package com.wyk.student.collection;


import com.wyk.student.domain.request.OrderQueryRequest;
import com.wyk.student.domain.vo.OrderVo;
import com.wyk.student.domain.vo.PageVo;
import com.wyk.student.service.OrderService;
import com.wyk.util.Request;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
@Tag(name = "订单管理")
@Validated
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "新增订单")
    @PostMapping
    public Request<Void> insertOrder() {
        orderService.insertOrder();
        return Request.ok("订单创建成功");
    }
    @Operation(summary = "删除订单")
    @DeleteMapping("/{orderId}")
    public Request<Void> deleteOrder(@NotNull(message = "orderId不能为空") @PathVariable Long orderId) {
        orderService.deleteOrder(orderId);
        return Request.ok("删除订单成功");
    }
    @Operation(summary = "查询当前用户订单p")
    @GetMapping
    public Request<PageVo<OrderVo>> selectOrder(@RequestParam(defaultValue = "1",required = false) Long pageNum,
                                                @RequestParam(defaultValue = "10",required = false) Long pageSize) {
        PageVo<OrderVo> orderVoPageVo = orderService.selectOrder(pageNum,pageSize);
        return Request.ok("删除订单失败",orderVoPageVo);
    }
    @Operation(summary = "查询所有用户订单p")
    @GetMapping("/list")
    public Request<PageVo<OrderVo>> selectAllOrder(@RequestParam(defaultValue = "1",required = false) Long pageNum,
                                                @RequestParam(defaultValue = "10",required = false) Long pageSize) {
        PageVo<OrderVo> orderVoPageVo = orderService.selectAllOrder(pageNum,pageSize);
        return Request.ok("查询所有订单成功",orderVoPageVo);
    }
    @Operation(summary = "条件查询订单p")
    @PostMapping("/list")
    public Request<PageVo<OrderVo>> selectAllOrder(@RequestParam(defaultValue = "1",required = false) Long pageNum,
                                                   @RequestParam(defaultValue = "10",required = false) Long pageSize,
                                                   @RequestBody OrderQueryRequest request) {
        PageVo<OrderVo> orderVoPageVo = orderService.selectOrderByRequest(pageNum,pageSize,request);
        return Request.ok("订单条件查询成功",orderVoPageVo);
    }
    @Operation(summary = "订单支付")
    @GetMapping("/pay")
    public Request<Void> orderPayment(@NotNull(message = "订单号不能为空") @RequestParam Long orderId) {
        orderService.orderPayment(orderId);
        return Request.ok("支付成功");
    }
}
