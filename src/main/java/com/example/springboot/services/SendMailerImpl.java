package com.example.springboot.services;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
@AllArgsConstructor
@Slf4j
public class SendMailerImpl implements SendMailer {
    private final JavaMailSender mailSender;

    @Override
    public void sendMail(String toAddress, String messageBody) {
        sendMessage(toAddress, messageBody);
    }

    //В случае возникновения ошибок при отправке сообщения пользователю на email, ошибка логируется
    //Чтобы уведомлять пользователя об ошибке, возможно, стоило добавить в БД сохранение неотправленных сообщений и пробовать их пересылать неким джобом,
    //либо также использовать rabbit, но по заданию messaging решение используется только в работе с внешней системой
    private void sendMessage(String toAddress, String messageBody) {
        try {
            if(shouldThrowTimeout()) {
                sleep();
                throw new TimeoutException("Timeout!");
            }

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("from@from.from");
            message.setTo(toAddress);
            message.setSubject("Login form");
            message.setText(messageBody);
            mailSender.send(message);

            if(shouldSleep()) {
                sleep();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        log.info("Message sent to {}, body {}.", toAddress, messageBody);
    }

    @SneakyThrows
    private static void sleep() {
        Thread.sleep(TimeUnit.SECONDS.toMillis(10));
    }

    private static boolean shouldSleep() {
        return new Random().nextInt(10) == 1;
    }

    private static boolean shouldThrowTimeout() {
        return new Random().nextInt(10) == 1;
    }
}
