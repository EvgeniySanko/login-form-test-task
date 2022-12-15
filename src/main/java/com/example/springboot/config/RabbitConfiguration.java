package com.example.springboot.config;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.support.RetryTemplate;

import static com.example.springboot.config.RabbitConstants.APP_EXCHANGE_NAME;
import static com.example.springboot.config.RabbitConstants.DEAD_LETTER_EXCHANGE_NAME;
import static com.example.springboot.config.RabbitConstants.LOGIN_FORM_DATA_QUEUE_NAME;
import static com.example.springboot.config.RabbitConstants.LOGIN_FORM_DATA_ROUTE_NAME;
import static com.example.springboot.config.RabbitConstants.OUTER_SYSTEM_ANSWER_DLQ_NAME;
import static com.example.springboot.config.RabbitConstants.OUTER_SYSTEM_ANSWER_QUEUE_NAME;
import static com.example.springboot.config.RabbitConstants.OUTER_SYSTEM_ANSWER_ROUTE_DLQ_NAME;
import static com.example.springboot.config.RabbitConstants.OUTER_SYSTEM_ANSWER_ROUTE_NAME;


@Configuration
public class RabbitConfiguration {

    //Exchange для ошибочных сообщений, DirectExchange т.к. использую разные ключи для выбора очереди
    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(DEAD_LETTER_EXCHANGE_NAME);
    }

    //Default Exchange
    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(APP_EXCHANGE_NAME);
    }

    //Очередь отправки данных формы во внешнюю систему
    @Bean
    public Queue loginFormDataQueue() {
        return QueueBuilder.durable(LOGIN_FORM_DATA_QUEUE_NAME).build();
    }

    //Binding для очереди отправки данных формы
    @Bean
    public Binding bindingLoginFormData() {
        return BindingBuilder.bind(loginFormDataQueue())
                .to(exchange())
                .with(LOGIN_FORM_DATA_ROUTE_NAME);
    }

    //Очередь для получения информации из внешней системы,
    // с настройкой изменения очереди в случае возникновения ошибки при обмене сообщениями
    @Bean
    public Queue outerSystemAnswerQueue() {
        return QueueBuilder.durable(OUTER_SYSTEM_ANSWER_QUEUE_NAME)
                .withArgument("x-dead-letter-exchange", DEAD_LETTER_EXCHANGE_NAME)
                .withArgument("x-dead-letter-routing-key", OUTER_SYSTEM_ANSWER_ROUTE_DLQ_NAME)
                .build();
    }

    //Binding для очереди получения информации из внешней системы
    @Bean
    public Binding bindingOuterSystemAnswer() {
        return BindingBuilder.bind(outerSystemAnswerQueue())
                .to(exchange())
                .with(OUTER_SYSTEM_ANSWER_ROUTE_NAME);
    }

    //Очередь для сообщений из внешней системы, обработанных с ошибкой
    @Bean
    Queue deadOuterSystemAnswerQueue() {
        return QueueBuilder.durable(OUTER_SYSTEM_ANSWER_DLQ_NAME).build();
    }

    //Binding для очереди сообщений из внешней системы, обработанных с ошибкой
    @Bean
    public Binding bindingDeadOuterSystemAnswer() {
        return BindingBuilder.bind(deadOuterSystemAnswerQueue())
                .to(deadLetterExchange())
                .with(OUTER_SYSTEM_ANSWER_ROUTE_DLQ_NAME);
    }

    @Bean
    public AmqpTemplate template(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setReplyTimeout(20000);
        return rabbitTemplate;
    }
}
