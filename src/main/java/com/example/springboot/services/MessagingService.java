package com.example.springboot.services;

import com.example.springboot.outerSystem.OuterSystemAnswer;
import org.springframework.messaging.Message;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public interface MessagingService<T, A> {
    /**
     * Отправка сообщения во внешнюю систему
     * @param data - сообщение для отправки
     * @return messageId
     */
    Long send(Message<T> data);

    /**
     * Обработка сообщения из внешней системы
     * @param data - сообщение для обработки
     * @return сообщение
     * @throws TimeoutException
     * @throws IOException
     */
    Message<T> receive(Message<A> data) throws TimeoutException;

    /**
     * Обработка сообщения из мертвой очереди
     * @param message - сообщение для обработки
     * @throws TimeoutException
     * @throws IOException
     */
    void processFailedMessages(Message<OuterSystemAnswer> message) throws TimeoutException;

    /**
     * Пытался собирать сообщения, чтобы в дальнейшем разбивать на задачи, в полном объеме реализовать не получилось,
     * поэтому данный код закомментирован
     *
     * Обработка сообщения из внешней системы. Разбивается на задачи фреймворком ветвления-объединения
     * @param data - сообщение для обработки
     * @param channel - канал очереди
     * @param tag - порядковый номер amqp_deliveryTag
     * @return сообщение
     * @throws TimeoutException
     * @throws IOException
     */
//    List<Message<LoginFormData>> receive(Message<A> data, Channel channel, long tag) throws TimeoutException;
}
