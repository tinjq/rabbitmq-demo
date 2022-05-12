package com.example.mq.rabbit.springboot.component;

import com.example.mq.rabbit.springboot.constant.RabbitConstant;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderUpdateReceiver {

   /**
    * 组合使用监听
    *
    * @param message
    * @param channel
    * @throws Exception
    * @RabbitListener @QueueBinding @Queue @Exchange
    */
   @RabbitListener(bindings = @QueueBinding(
           value = @Queue(value = RabbitConstant.QUEUE_ORDER_UPDATE, durable = "true"),
           exchange = @Exchange(name = RabbitConstant.EXCHANGE, durable = "true", type = "topic", ignoreDeclarationExceptions = "true"),
           key = RabbitConstant.QUEUE_ORDER_UPDATE
   ))
   @RabbitHandler
   public void onMessage(Message message, Channel channel) throws Exception {
       //	1. 收到消息以后进行业务端消费处理
       log.info("-----------------------");
       log.info("消费消息:" + message.getPayload());

       //  2. 处理成功之后 获取deliveryTag 并进行手工的ACK操作, 因为我们配置文件里配置的是 手工签收
       //	spring.rabbitmq.listener.simple.acknowledge-mode=manual
       Long deliveryTag = (Long) message.getHeaders().get(AmqpHeaders.DELIVERY_TAG);
       channel.basicAck(deliveryTag, false);
   }
}
