package com.example.springboot.web;

import com.example.springboot.outerSystem.OuterSystemAnswer;
import com.example.springboot.db.entity.LoginFormData;
import com.example.springboot.services.MessagingService;
import com.example.springboot.web.converter.LoginFormDataConverter;
import com.example.springboot.web.dto.LoginFormDataDto;
import lombok.AllArgsConstructor;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class LoginRestController {
    private final MessagingService<LoginFormData, OuterSystemAnswer> messagingService;
    private final LoginFormDataConverter converter;

    @PostMapping("/send")
    public Long send(@RequestBody LoginFormDataDto dto) {
        LoginFormData loginFormData = converter.convertToEntity(dto);
        return messagingService.send(new GenericMessage<>(loginFormData));
    }
}
