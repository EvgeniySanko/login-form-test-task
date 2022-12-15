package com.example.springboot.services;

import com.example.springboot.outerSystem.OuterSystemAnswer;
import com.example.springboot.db.entity.LoginFormData;
import com.rabbitmq.client.Channel;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import static com.example.springboot.config.RabbitConstants.APP_EXCHANGE_NAME;
import static com.example.springboot.config.RabbitConstants.LOGIN_FORM_DATA_ROUTE_NAME;
import static com.example.springboot.config.RabbitConstants.OUTER_SYSTEM_ANSWER_DLQ_NAME;
import static com.example.springboot.config.RabbitConstants.OUTER_SYSTEM_ANSWER_QUEUE_NAME;

@Service
@AllArgsConstructor
@Slf4j
public class MessagingServiceImpl implements MessagingService<LoginFormData, OuterSystemAnswer> {
    private final LoginFormDataService loginFormDataService;
    private final RabbitTemplate rabbitTemplate;
    private final SendMailer sendMailer;

//    private final Map<Long, TaskDto> tempStore = new ConcurrentHashMap<>();

    @Override
    public Long send(Message<LoginFormData> data) {
        LoginFormData savedData = loginFormDataService.save(data.getPayload());
        if (Objects.nonNull(savedData)) {
            rabbitTemplate.convertAndSend(APP_EXCHANGE_NAME, LOGIN_FORM_DATA_ROUTE_NAME, new GenericMessage<>(savedData));
        }
        return savedData.getId();
    }

    @Override
    @RabbitListener(queues = OUTER_SYSTEM_ANSWER_QUEUE_NAME)
    public Message<LoginFormData> receive(Message<OuterSystemAnswer> message,
                                          Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws TimeoutException, IOException {

        LoginFormData loginFormData = loginFormDataService.findById(message.getPayload().getId());

        if (shouldThrowTimeout()) {
            sleep();
            throw new TimeoutException("Timeout!");
        }

        sendMailer.sendMail(loginFormData.getEmail(), message.getPayload().getMessageType() + " " + loginFormData.getId());

        if (shouldSleep()) {
            sleep();
        }
        //Подтверждение отправки сообщения
        channel.basicAck(tag, false);

        return new GenericMessage<>(loginFormData);
    }

//    @Override
//    @RabbitListener(queues = OUTER_SYSTEM_ANSWER_QUEUE_NAME)
//    public List<Message<LoginFormData>> receive(Message<OuterSystemAnswer> message,
//                                                Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws TimeoutException, IOException {
//
//        tempStore.put(message.getPayload().getId(), new TaskDto(message.getPayload().getId(), message, channel, tag));
//        System.out.println("Stored " + message.getPayload());
//        System.out.println(tempStore.size());
//        ForkJoinReceiveData task = new ForkJoinReceiveData(tempStore, loginFormDataService, sendMailer);
//        List<LoginFormData> invokeData = new ForkJoinPool().invoke(task);
//        invokeData.forEach(data -> tempStore.remove(data.getId()));
//        return invokeData.stream().map(GenericMessage::new).collect(Collectors.toList());
//    }

    //Если необходимо отправлять ошибочные сообщения в мертвую очередь,
    // в application.properties необходимо закомментировать 25,26 строки
    @RabbitListener(queues = OUTER_SYSTEM_ANSWER_DLQ_NAME)
    public void processFailedMessages(Message<OuterSystemAnswer> message) {
        log.info("Received failed message: {}", message.toString());
    }

    @SneakyThrows
    private static void sleep() {
        Thread.sleep(TimeUnit.MINUTES.toMillis(1));
    }

    private static boolean shouldSleep() {
        return new Random().nextInt(10) == 1;
    }

    private static boolean shouldThrowTimeout() {
        return new Random().nextInt(10) == 1;
    }
}
