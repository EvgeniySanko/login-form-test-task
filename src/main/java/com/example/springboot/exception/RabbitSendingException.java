package com.example.springboot.exception;

public class RabbitSendingException extends RuntimeException {
    public RabbitSendingException(String message, Throwable cause) {
        super(message, cause);
    }
}
