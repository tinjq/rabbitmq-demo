package com.example.mq.rabbit.springboot.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
public class RabbitSender {

    // @Autowired
    // private RabbitTemplate rabbitTemplate;
    @Resource
    private ConnectionFactory connectionFactory;

    public RabbitTemplate getRabbitTemplate() {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMandatory(true);
        return rabbitTemplate;
    }

    /**
     * 这里就是确认消息的回调监听接口，用于确认消息是否被broker所收到
     */
    // final RabbitTemplate.ConfirmCallback confirmCallback = new RabbitTemplate.ConfirmCallback() {
    //     /**
    //      *    @param ack broker 是否落盘成功
    //      *    @param cause 失败的一些异常信息
    //      */
    //     @Override
    //     public void confirm(CorrelationData correlationData, boolean ack, String cause) {
    //         System.err.println("消息ACK结果:" + ack + ", correlationData: " + correlationData.getId());
    //     }
    // };

    /**
     * 对外发送消息的方法
     *
     * @param message    具体的消息内容
     * @param properties 额外的附加属性
     * @throws Exception
     */
    public void send(String exchange, String routingKey, Object message, Map<String, Object> properties,
                     RabbitTemplate.ConfirmCallback confirmCallback) {

        MessageHeaders mhs = new MessageHeaders(properties);
        Message<?> msg = MessageBuilder.createMessage(message, mhs);

        RabbitTemplate rabbitTemplate = getRabbitTemplate();
        rabbitTemplate.setConfirmCallback(confirmCallback);

        // 	指定业务唯一的iD
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());

        MessagePostProcessor mpp = new MessagePostProcessor() {

            @Override
            public org.springframework.amqp.core.Message postProcessMessage(org.springframework.amqp.core.Message message)
                    throws AmqpException {
                System.err.println("---> post to do: " + message);
                return message;
            }
        };

        rabbitTemplate.convertAndSend(exchange, routingKey, msg, mpp, correlationData);

    }

    public void send(String exchange, String routingKey, Object message, RabbitTemplate.ConfirmCallback confirmCallback) {
        send(exchange, routingKey, message, new HashMap<>(), confirmCallback);
    }


}
