package com.example.springboot.web;

import com.example.springboot.db.entity.LoginFormData;
import com.example.springboot.exception.ExceptionResponse;
import com.example.springboot.outerSystem.OuterSystemAnswer;
import com.example.springboot.services.MessagingService;
import com.example.springboot.web.dto.LoginFormDataDto;
import com.example.springboot.web.mappers.LoginFormDataMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@AllArgsConstructor
public class LoginRestController {
    private final MessagingService<LoginFormData, OuterSystemAnswer> messagingService;
    private LoginFormDataMapper mapper;

    @PostMapping("/send")
    public Long send(@RequestBody LoginFormDataDto dto) {
        LoginFormData loginFormData = mapper.dtoToEntity(dto);
        return messagingService.send(new GenericMessage<>(loginFormData));
    }

    @ExceptionHandler({ Exception.class })
    public ResponseEntity<ExceptionResponse> handleException(Exception e) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(e.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
