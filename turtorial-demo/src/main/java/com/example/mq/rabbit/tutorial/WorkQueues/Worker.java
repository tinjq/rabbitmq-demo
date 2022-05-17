package com.example.mq.rabbit.tutorial.WorkQueues;

import com.alibaba.fastjson.JSON;
import com.example.mq.rabbit.tutorial.constant.RabbitConstant;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

@Slf4j
public class Worker {

    private static final String TASK_QUEUE_NAME = "task_queue";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(RabbitConstant.HOST);
        final Connection connection = factory.newConnection();
        final Channel channel = connection.createChannel();

        channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        channel.basicQos(1);

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);

            log.info(" [x] Received message:{}, delivery:{}", message, JSON.toJSONString(delivery));
            try {
                doWork(message);
                // throw new RuntimeException("sdf");
                log.info(" [x] Done");
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            } finally {
                // log.info(" [x] Done");
                // channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            }
        };

        log.info("basicConsume");
        channel.basicConsume(TASK_QUEUE_NAME, false, deliverCallback, consumerTag -> { });
    }

    private static void doWork(String task) {
        for (char ch : task.toCharArray()) {
            if (ch == '.') {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException _ignored) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}
