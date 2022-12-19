package com.example.springboot.services;

import com.example.springboot.exception.RabbitSendingException;
import com.example.springboot.outerSystem.OuterSystemAnswer;
import com.example.springboot.db.entity.LoginFormData;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
        try {
            rabbitTemplate.convertAndSend(APP_EXCHANGE_NAME, LOGIN_FORM_DATA_ROUTE_NAME, new GenericMessage<>(savedData));
        } catch (Exception e) {
            //В случае неуспешной отправки, ошибка будет обработана GlobalControllerExceptionHandler,
            // таким образом пользователь будет уведомлен о неуспешной отправке
            String message = String.format("Form was not send. Data with id = %s ", savedData.getId());
            log.error(message, e);
            throw new RabbitSendingException("Form was not send.", e);
        }
        return savedData.getId();
    }

    //При возникновении ошибок во время обработки сообщения,
    // сообщение будет переотправлено на обработку еще некоторое количество раз в зависимости от значения,
    // указанного в application.properties (spring.rabbitmq.listener.simple.retry.max-attempts), на данный момент 3 раза
    // и интервалом между повторами (spring.rabbitmq.listener.simple.retry.initial-interval), на данный момент 120 сек
    //В случае, если во время последнего повтора отправки возникает exception, сообщение будет отправлено в мертвую очередь OUTER_SYSTEM_ANSWER_DLQ_NAME
    @Override
    @RabbitListener(queues = OUTER_SYSTEM_ANSWER_QUEUE_NAME)
    public Message<LoginFormData> receive(Message<OuterSystemAnswer> message) throws TimeoutException {

        LoginFormData loginFormData = loginFormDataService.findById(message.getPayload().getId());

        if (shouldThrowTimeout()) {
            sleep();
            throw new TimeoutException("Timeout!");
        }

        if (shouldSleep()) {
            sleep();
        }

        sendMailer.sendMail(loginFormData.getEmail(), message.getPayload().getMessageType().toString());

        return new GenericMessage<>(loginFormData);
    }

    @Override
    @RabbitListener(queues = OUTER_SYSTEM_ANSWER_DLQ_NAME)
    public void processFailedMessages(Message<OuterSystemAnswer> message) {
        log.error("Received failed message: {}", message.toString());
        LoginFormData loginFormData = loginFormDataService.findById(message.getPayload().getId());
        sendMailer.sendMail(loginFormData.getEmail(), "Form was not send.");
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
