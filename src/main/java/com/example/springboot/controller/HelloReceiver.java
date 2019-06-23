package com.example.springboot.controller;

import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class HelloReceiver {

    @RabbitListener(queues = "hello")
    @RabbitHandler
    public void process(String hello, Channel channel, Message message) {
        System.out.println("Receiver  : " + new String(message.getBody()));
        try {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}

