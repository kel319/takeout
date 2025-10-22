package com.wyk.student.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class RabbitConfig {
    private static final String FRONT_EXCHANGE = "front.direct";
    private static final String DEAD_EXCHANGE = "dead.direct";
    private static final String RETRY_EXCHANGE = "retry.direct";
    private static final String FINAL_EXCHANGE = "final.direct";
    private static final String DELAY_QUEUE = "delay.queue";
    private static final String DEAD_QUEUE = "dead.queue";
    private static final String RETRY_QUEUE = "retry.queue";
    private static final String FINAL_QUEUE = "final.queue";
    private static final String DEAD_ROUTE = "dead";
    private static final String DELAY_ROUTE = "delay";
    private static final String RETRY_ROUTE = "retry";
    private static final String FINAL_ROUTE = "final";

    @Bean
    public Queue delayQueue() {
        return QueueBuilder
                .durable(DELAY_QUEUE)
                .deadLetterExchange(DEAD_EXCHANGE)
                .deadLetterRoutingKey(DEAD_ROUTE)
                .build();
    }
    @Bean
    public Queue deadQueue() {
        return QueueBuilder
                .durable(DEAD_QUEUE)
                .build();
    }
    @Bean
    public Queue retryQueue() {
        return QueueBuilder
                .durable(RETRY_QUEUE)
                .deadLetterExchange(DEAD_EXCHANGE)
                .deadLetterRoutingKey(DEAD_ROUTE)
                .build();
    }
    @Bean
    public Queue finalQueue() {
        return QueueBuilder
                .durable(FINAL_QUEUE)
                .build();
    }
    @Bean
    public DirectExchange deadExchange() {
        return ExchangeBuilder.directExchange(DEAD_EXCHANGE).build();
    }

    @Bean
    public DirectExchange frontExchange() {
        return ExchangeBuilder.directExchange(FRONT_EXCHANGE).build();
    }

    @Bean
    public DirectExchange retryExchange() {
        return ExchangeBuilder.directExchange(RETRY_EXCHANGE).build();
    }


    @Bean
    public DirectExchange finalExchange() {
        return ExchangeBuilder.directExchange(FINAL_EXCHANGE).build();
    }

    @Bean
    public Binding delayBinding(Queue delayQueue, DirectExchange frontExchange) {
        return BindingBuilder.bind(delayQueue).to(frontExchange).with(DELAY_ROUTE);
    }
    @Bean
    public Binding deadBinding(Queue deadQueue, DirectExchange deadExchange) {
        return BindingBuilder.bind(deadQueue).to(deadExchange).with(DEAD_ROUTE);
    }
    @Bean
    public Binding retryBinding(Queue retryQueue, DirectExchange retryExchange) {
        return BindingBuilder.bind(retryQueue).to(retryExchange).with(RETRY_ROUTE);
    }
    @Bean
    public Binding finalBinding(Queue finalQueue, DirectExchange finalExchange) {
        return BindingBuilder.bind(finalQueue).to(finalExchange).with(FINAL_ROUTE);
    }


    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    @Bean
    public RabbitTemplate rabbitTemplate(
            CachingConnectionFactory connectionFactory
            ,Jackson2JsonMessageConverter jsonMessageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter);
        rabbitTemplate.setConfirmCallback
                ((CorrelationData data,boolean ack,String cause) -> {
                    if (ack) {
                        log.info("消息成功发送至交换机,CorrelationData: {}",data);
                    } else {
                        log.warn("消息未发送至交换机: {},CorrelationData: {}",cause,data);
                    }
                });
        rabbitTemplate.setReturnsCallback(returnMsg -> {
            log.warn("消息路由队列失败");
            log.warn("RoutingKey: {}",returnMsg.getRoutingKey());
            log.warn("ReplyCode: {}",returnMsg.getReplyCode());
            log.warn("Message: {}",returnMsg.getMessage());
            log.warn("Exchange: {}",returnMsg.getExchange());
            log.warn("ReplyText: {}",returnMsg.getReplyText());
        });


        return rabbitTemplate;
    }
}
