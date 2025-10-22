package com.wyk.student.util;


import com.rabbitmq.client.Channel;
import com.wyk.exception.CustomizeException;
import com.wyk.student.domain.vo.PageVo;
import com.wyk.student.domain.vo.UserVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class RabbitUtil {

    private static final String RETRY_EXCHANGE = "retry.direct";
    private static final String RETRY_ROUTE = "retry";

    private final RabbitTemplate rabbitTemplate;

    public <T> void send(String exchange,String routerKey,String expiration,T message,String uuid) {
        CorrelationData cd = new CorrelationData(uuid);
        cd.getFuture().whenComplete((result,ex) -> {
            if (ex == null) {
                log.debug("已收到Broker确认消息");
                if (result.isAck()) {
                    log.debug("消息已发送至Broker");
                } else {
                    log.warn("消息被Broker拒绝,exchange: {},routerKey: {},UUID: {}",exchange,routerKey,uuid);
                }
            } else {
                log.warn("未收到Broker确认消息,exchange: {},routerKey: {},UUID: {},error: {},",exchange,routerKey,uuid,ex.getMessage());
            }
        });
        rabbitTemplate.convertAndSend(exchange,routerKey,message,msg -> {
            msg.getMessageProperties().setExpiration(expiration);
            return msg;
        },cd);
    }
    public String getCorrelationData() {
        return UUID.randomUUID().toString();
    }


    public <T> void manualFunction(T msg,Message message, Channel channel, RabbitMQFun<T> manual) {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        log.debug("消息队列开始执行,deliveryTag: {}",deliveryTag);
        try {
            manual.execute(msg);
            channel.basicAck(deliveryTag,false);
        } catch (IOException e) {
            exceptionGroupFromMQ("确认消息时发生IO异常",deliveryTag,e);
            int retryCount = (int) Optional.ofNullable(message.getMessageProperties().getHeader("retryCount")).orElse(0);
            if (retryCount < 3) {
                log.warn("消息第{}次重试",retryCount+1);
                rabbitTemplate.convertAndSend(RETRY_EXCHANGE, RETRY_ROUTE, msg, mes -> {
                    mes.getMessageProperties().setExpiration("3000");
                    mes.getMessageProperties().setHeader("retryCount", retryCount + 1);
                    return mes;
                });
                rejectionHandler(deliveryTag,channel);
            } else {
                log.warn("消息重试次数达到上限,已拒绝");
                rejectionHandler(deliveryTag, channel);
            }
        } catch (CustomizeException e) {
            exceptionGroupFromMQ("消息队列中发生业务异常",deliveryTag,e);
            rejectionHandler(deliveryTag,channel);
        } catch (Exception e) {
            exceptionGroupFromMQ("未知异常",deliveryTag,e);
            rejectionHandler(deliveryTag,channel);
        }
    }
    private void rejectionHandler(long deliveryTag, Channel channel) {
        try {
            channel.basicNack(deliveryTag,false,false);
        } catch (IOException ex) {
            log.warn("消息拒绝失败,deliveryTag: {},error: {}",deliveryTag,ex.getMessage());
            log.debug("消息拒绝失败,deliveryTag: {}",deliveryTag,ex);
        }
    }
    private void exceptionGroupFromMQ(String err, Long deliveryTag,Throwable e) {
        log.warn("{},deliveryTag: {},error: {}",err,deliveryTag,e.getMessage());
        log.debug("{},deliveryTag: {}",err,deliveryTag,e);
    }
}
