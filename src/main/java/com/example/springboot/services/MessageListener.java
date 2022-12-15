package com.example.springboot.services;

import com.rabbitmq.client.Channel;
import org.springframework.messaging.Message;

import java.io.IOException;

public interface MessageListener<T> {
    void handleMessage(Message<T> incomingMessage, Channel channel, long tag) throws IOException;
}
