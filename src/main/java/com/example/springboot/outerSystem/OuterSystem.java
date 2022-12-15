package com.example.springboot.outerSystem;

import com.example.springboot.outerSystem.OuterSystemAnswer;
import com.example.springboot.db.entity.LoginFormData;
import com.example.springboot.services.MessageListener;
import com.example.springboot.services.MessageType;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Random;

import static com.example.springboot.config.RabbitConstants.APP_EXCHANGE_NAME;
import static com.example.springboot.config.RabbitConstants.LOGIN_FORM_DATA_QUEUE_NAME;
import static com.example.springboot.config.RabbitConstants.OUTER_SYSTEM_ANSWER_ROUTE_NAME;

/**
 * Внешняя система, используется для принятия решения
 * о принятии или отклонении данных формы
 */
@Component
public class OuterSystem implements MessageListener<LoginFormData> {
    private final RabbitTemplate rabbitTemplate;

    public OuterSystem(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Выполняет обработку данных формы
     * @param incomingMessage входные данные о форме
     * MessageType.FAIL - форма отклонена
     * MessageType.OK - форма принята
     */
    @Override
    @RabbitListener(queues = LOGIN_FORM_DATA_QUEUE_NAME)
    public void handleMessage(Message<LoginFormData> incomingMessage,
                              Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        LoginFormData payload = incomingMessage.getPayload();

        if (shouldDecline()) {
            rabbitTemplate.convertSendAndReceive(
                    APP_EXCHANGE_NAME,
                    OUTER_SYSTEM_ANSWER_ROUTE_NAME,
                    new GenericMessage<>(new OuterSystemAnswer(payload.getId(), MessageType.FAIL)));
            channel.basicAck(tag, false);
            return;
        }
        rabbitTemplate.convertSendAndReceive(
                APP_EXCHANGE_NAME,
                OUTER_SYSTEM_ANSWER_ROUTE_NAME,
                new GenericMessage<>(new OuterSystemAnswer(payload.getId(), MessageType.OK)));
        channel.basicAck(tag, false);
    }

    // Логика принятие решения об отклонении или принятии формы
    private static boolean shouldDecline() {
        return new Random().nextInt(10) == 1;
    }
}
