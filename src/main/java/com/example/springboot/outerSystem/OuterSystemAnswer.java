package com.example.springboot.outerSystem;

import com.example.springboot.services.MessageType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@AllArgsConstructor
@Data
public class OuterSystemAnswer implements Serializable {
    private Long id;
    private MessageType messageType;
}
