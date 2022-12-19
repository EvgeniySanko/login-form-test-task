package com.example.springboot.services;

import org.springframework.messaging.Message;

public interface MessageListener<T> {
    void handleMessage(Message<T> incomingMessage);
}
