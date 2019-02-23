package com.kiss.kissnest.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class RabbitAgent {

    private RabbitTemplate rabbitTemplate;

    public static final String MONITOR_EXCHANGE = "kiss";
    public static final String MONITOR_ROUTINGKEY = "monitor";

    /**
     * 构造方法注入
     */
    @Autowired
    public RabbitAgent(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void execute(String content, String exchange, String routingkey) {
        String msgId = UUID.randomUUID().toString();
        CorrelationData correlationId = new CorrelationData(msgId);
        Message message = MessageBuilder.withBody(content.getBytes())
                .setContentType(MessageProperties.CONTENT_TYPE_TEXT_PLAIN)
                .setCorrelationId(msgId).build();
        rabbitTemplate.convertAndSend(exchange, routingkey, message, correlationId);
    }

    public void monitorLog(String content) {

        try {
            execute(content, MONITOR_EXCHANGE, MONITOR_ROUTINGKEY);
        } catch (Exception e) {
            log.warn("\"rabbitAgent\":{\"content\":{}}", content);
        }

    }

}
