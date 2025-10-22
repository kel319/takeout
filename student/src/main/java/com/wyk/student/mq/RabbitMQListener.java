package com.wyk.student.mq;


import com.rabbitmq.client.Channel;
import com.wyk.student.service.OrderService;
import com.wyk.student.util.RabbitUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;


//@Component
@RequiredArgsConstructor
public class RabbitMQListener {

    private final RabbitUtil rabbitUtil;
    private final OrderService orderService;

    @RabbitListener(queues = "dead.queue", ackMode = "MANUAL")
    public void deadListener(Long userId, Message message, Channel channel) {
        rabbitUtil.manualFunction(userId,message,channel,orderService::orderHandler);
    }
}
