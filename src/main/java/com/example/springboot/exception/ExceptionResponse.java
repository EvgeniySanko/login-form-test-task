package com.example.springboot.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
@Builder
public class ExceptionResponse {
    private String message;
    private LocalDateTime dateTime;
}
