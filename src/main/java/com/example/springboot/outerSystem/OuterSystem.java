package com.example.springboot.outerSystem;

import com.example.springboot.db.entity.LoginFormData;
import com.example.springboot.services.MessageListener;
import com.example.springboot.services.MessageType;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;

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
    public void handleMessage(Message<LoginFormData> incomingMessage) {
        LoginFormData payload = incomingMessage.getPayload();

        if (shouldDecline()) {
            rabbitTemplate.convertSendAndReceive(
                    APP_EXCHANGE_NAME,
                    OUTER_SYSTEM_ANSWER_ROUTE_NAME,
                    new GenericMessage<>(new OuterSystemAnswer(payload.getId(), MessageType.FAIL)));
            return;
        }
        rabbitTemplate.convertSendAndReceive(
                APP_EXCHANGE_NAME,
                OUTER_SYSTEM_ANSWER_ROUTE_NAME,
                new GenericMessage<>(new OuterSystemAnswer(payload.getId(), MessageType.OK)));
    }

    // Логика принятие решения об отклонении или принятии формы
    private static boolean shouldDecline() {
        return new Random().nextInt(10) == 1;
    }
}
