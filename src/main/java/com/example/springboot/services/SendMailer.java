package com.example.springboot.services;

import java.util.concurrent.TimeoutException;

public interface SendMailer {
    void sendMail (String toAddress, String messageBody) throws TimeoutException;
}
