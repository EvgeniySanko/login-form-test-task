package com.example.springboot.exception;

public class DataNotFoundException extends RuntimeException {
    public DataNotFoundException(Long id) {
        super(String.format("Data with id = %d not found.", id));
    }
}
