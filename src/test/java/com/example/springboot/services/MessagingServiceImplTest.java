package com.example.springboot.services;

import com.example.springboot.db.entity.LoginFormData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static com.example.springboot.config.RabbitConstants.APP_EXCHANGE_NAME;
import static com.example.springboot.config.RabbitConstants.LOGIN_FORM_DATA_ROUTE_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class MessagingServiceImplTest {
    @InjectMocks
    private MessagingServiceImpl messagingService;
    @Mock
    private RabbitTemplate template;
    @Mock
    private LoginFormDataService loginFormDataService;
    @Mock
    private SendMailer sendMailer;

    @Captor
    private ArgumentCaptor<GenericMessage<LoginFormData>> genericMessageCaptor;

    @Test
    void sendTest() {
        LoginFormData loginFormData = getLoginFormData();
        when(loginFormDataService.save(any())).thenReturn(loginFormData);
        doNothing().when(template).convertAndSend(anyString(), anyString(), any(Message.class));

        GenericMessage<LoginFormData> data = new GenericMessage<>(loginFormData);
        messagingService.send(data);

        verify(template).convertAndSend(eq(APP_EXCHANGE_NAME), eq(LOGIN_FORM_DATA_ROUTE_NAME), genericMessageCaptor.capture());
        GenericMessage<LoginFormData> value = genericMessageCaptor.getValue();
        assertEquals(1L, value.getPayload().getId());
        assertEquals("login", value.getPayload().getLogin());
        assertEquals("email", value.getPayload().getEmail());
        assertEquals("fio", value.getPayload().getFio());
        assertEquals("password", value.getPayload().getPassword());
        verify(loginFormDataService).save(eq(loginFormData));
    }


    private static LoginFormData getLoginFormData() {
        return LoginFormData.builder()
                .id(1L)
                .login("login")
                .email("email")
                .fio("fio")
                .password("password")
                .build();
    }
}