package com.example.mq.rabbit.springboot;

import com.example.mq.rabbit.springboot.component.RabbitSender;
import com.example.mq.rabbit.springboot.constant.RabbitConstant;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Slf4j
@SpringBootTest
public class RabbitmqTest {

    @Resource
    private RabbitSender rabbitSender;

    @Test
    void sendTest() throws InterruptedException {
        String message = "Hello " + LocalDateTime.now();
        rabbitSender.send(RabbitConstant.EXCHANGE, "springboot.test", message, (correlationData, ack, cause) -> {
            if (ack) {
                log.info("send success:{}", correlationData.getId());
            } else {
                log.error("send fail:{}, cause:{}", correlationData.getId(), cause);
            }
        });

        TimeUnit.SECONDS.sleep(3);
    }

}
