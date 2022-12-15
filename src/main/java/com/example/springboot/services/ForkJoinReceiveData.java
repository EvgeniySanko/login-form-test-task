package com.example.springboot.services;

import com.example.springboot.db.entity.LoginFormData;
import com.example.springboot.outerSystem.OuterSystemAnswer;
import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import org.springframework.messaging.Message;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

/**
 * Разделяет входящие сообщения на отдельные задачи
 */
public class ForkJoinReceiveData extends RecursiveTask<List<LoginFormData>> {
    private final Map<Long, TaskDto> data;
    private static List<LoginFormData> result = new ArrayList<>();

    private LoginFormDataService loginFormDataService;
    private SendMailer sendMailer;

    public ForkJoinReceiveData(Map<Long, TaskDto> data, LoginFormDataService loginFormDataService, SendMailer sendMailer) {
        this.data = new ConcurrentHashMap<>(data);
        this.loginFormDataService = loginFormDataService;
        this.sendMailer = sendMailer;
    }

    private ForkJoinReceiveData(Map<Long, TaskDto> data, SendMailer sendMailer, LoginFormDataService loginFormDataService) {
        this.data = new ConcurrentHashMap<>(data);
        this.loginFormDataService = loginFormDataService;
        this.sendMailer = sendMailer;
    }


    @Override
    protected List<LoginFormData> compute() {
        if (data.size() == 0) {
            return result;
        }
        if (data.size() == 1) {
            TaskDto first = data.values().stream().findFirst().get();
            data.clear();
            result.add(receive(first.getId(), first.getMessage(), first.getChannel(), first.getTag()));
            return result;
        }

        ForkJoinReceiveData leftTask = new ForkJoinReceiveData(
                getNElements(data, data.size() / 2, 0), this.sendMailer, this.loginFormDataService);
        leftTask.fork();
        System.out.println("leftTask run....");

        ForkJoinReceiveData rightTask = new ForkJoinReceiveData(
                getNElements(data, data.size() / 2, data.size() / 2), this.sendMailer, this.loginFormDataService);

        List<LoginFormData> rightResult = rightTask.compute();
        System.out.println("rightTask run....");
        List<LoginFormData> leftResult = leftTask.join();
        result.addAll(rightResult);
        System.out.println("rightResult = " + rightResult);
        result.addAll(leftResult);
        System.out.println("leftResult = " + leftResult);
        return result;
    }

    /**
     * Алгоритм последовательного выполнения задачи
     * @param id id сщщбщения
     * @param message входящее сообщение
     * @param channel канал очереди
     * @param tag
     * @return обработанные данные сообщения
     */
    private LoginFormData receive(Long id, Message<OuterSystemAnswer> message, Channel channel, long tag) {
        LoginFormData loginFormData = null;
        try {
            loginFormData = loginFormDataService.findById(id);

            if (shouldThrowTimeout()) {
                sleep();
                throw new TimeoutException("Timeout!");
            }

//            sendMailer.sendMail(loginFormData.getEmail(), message.getPayload().getMessageType());

            if (shouldSleep()) {
                sleep();
            }
            //Подтверждение отправки сообщения
            channel.basicAck(tag, false);
            System.out.println("RECEIVE " + loginFormData.getId());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        return loginFormData;
    }

    private Map<Long, TaskDto> getNElements(Map<Long, TaskDto> map, long n, long skip) {
        return map.entrySet()
                .stream()
                .skip(skip)
                .limit(n)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (v1,v2) -> v1, LinkedHashMap::new));
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
