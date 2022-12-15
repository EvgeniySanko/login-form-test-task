package com.example.springboot.services;

import com.example.springboot.outerSystem.OuterSystemAnswer;
import com.rabbitmq.client.Channel;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.messaging.Message;

@AllArgsConstructor
@Data
public class TaskDto {
    private Long id;
    private Message<OuterSystemAnswer> message;
    private Channel channel;
    private Long tag;
}
