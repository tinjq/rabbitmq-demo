package com.example.mq.rabbit.tutorial.WorkQueues;

import com.alibaba.fastjson.JSON;
import com.example.mq.rabbit.tutorial.constant.RabbitConstant;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import com.rabbitmq.client.Return;
import com.rabbitmq.client.ReturnCallback;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

public class NewTask {

    private static final String TASK_QUEUE_NAME = "task_queue";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(RabbitConstant.HOST);
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);
            channel.addConfirmListener((deliveryTag, multiple) -> {
                System.out.println("ack :" + deliveryTag);
            }, (deliveryTag, multiple) -> {
                System.out.println("nack :" + deliveryTag);
            });
            channel.addReturnListener(returnMessage -> {
                System.out.println("ReturnListener:" + JSON.toJSONString(returnMessage));
            });

            channel.confirmSelect();

            for (int i = 0; i < 1; i++) {
                send(channel, i + " hello...");
            }
        }
    }

    public static void send(Channel channel, String message) throws Exception {
        channel.basicPublish("", "TASK_QUEUE_NAME", true,
                MessageProperties.PERSISTENT_TEXT_PLAIN, // 消息 PERSISTENT
                message.getBytes(StandardCharsets.UTF_8));
        boolean waitForConfirms = channel.waitForConfirms(5000);
        System.out.println("waitForConfirms:" + waitForConfirms);
        System.out.println(" [x] Sent '" + message + "'");
    }
}
