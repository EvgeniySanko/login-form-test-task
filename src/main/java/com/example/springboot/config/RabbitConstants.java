package com.example.springboot.config;

public class RabbitConstants {
    public final static String LOGIN_FORM_DATA_QUEUE_NAME = "LOGIN_FORM_DATA_QUEUE";
    public final static String OUTER_SYSTEM_ANSWER_QUEUE_NAME = "OUTER_SYSTEM_ANSWER_QUEUE";
    public final static String OUTER_SYSTEM_ANSWER_DLQ_NAME = "OUTER_SYSTEM_ANSWER_DLQ";
    public final static String APP_EXCHANGE_NAME = "APP_EXCHANGE";
    public final static String DEAD_LETTER_EXCHANGE_NAME = "DEAD_LETTER_EXCHANGE";
    public final static String LOGIN_FORM_DATA_ROUTE_NAME = "LOGIN_FORM_DATA_ROUTE";
    public final static String OUTER_SYSTEM_ANSWER_ROUTE_NAME = "OUTER_SYSTEM_ANSWER_ROUTE";
    public final static String OUTER_SYSTEM_ANSWER_ROUTE_DLQ_NAME = "OUTER_SYSTEM_ANSWER_ROUTE_DLQ";
}
