package com.example.springboot.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalControllerExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(RabbitSendingException.class)
    public ResponseEntity<ExceptionResponse> handleRabbitSendingException(RabbitSendingException rabbitSendingException) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(rabbitSendingException.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
