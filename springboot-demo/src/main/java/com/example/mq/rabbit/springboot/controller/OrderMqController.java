package com.example.mq.rabbit.springboot.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSONObject;
import com.example.mq.rabbit.springboot.component.RabbitSender;
import com.example.mq.rabbit.springboot.constant.RabbitConstant;

@RestController
@RequestMapping("/mq/order")
public class OrderMqController {

    @Resource
    private RabbitSender rabbitSender;
    
    @GetMapping("/create/{message}")
    public String create(@PathVariable("message") String message) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("module", "order");
        jsonObject.put("type", "create");
        jsonObject.put("msg", message);
        
        rabbitSender.send(RabbitConstant.EXCHANGE, RabbitConstant.QUEUE_ORDER_CREATE, jsonObject.toJSONString(), 
            (correlationData, ack, cause) -> {
                if (ack) {
                    try {
                        TimeUnit.SECONDS.sleep(2);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.err.println("ack:" + ack + ", correlationData: " + correlationData.getId());
                } else {
                    System.err.println("ack:" + ack + ", correlationData: " + correlationData.getId() + ", cause:" + cause);
                }
            }
        );
        return "success";
    }

    @GetMapping("/update/{message}")
    public String update(@PathVariable("message") String message) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("module", "order");
        jsonObject.put("type", "update");
        jsonObject.put("msg", message);
        
        rabbitSender.send(RabbitConstant.EXCHANGE, RabbitConstant.QUEUE_ORDER_UPDATE, jsonObject.toJSONString(), 
            (correlationData, ack, cause) -> {
                if (ack) {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.err.println("ack:" + ack + ", correlationData: " + correlationData.getId());
                } else {
                    System.err.println("ack:" + ack + ", correlationData: " + correlationData.getId() + ", cause:" + cause);
                }
            }
        );
        return "success";
    }
}
