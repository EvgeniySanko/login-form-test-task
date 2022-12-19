package com.example.springboot.web.converter;

import com.example.springboot.db.entity.LoginFormData;
import com.example.springboot.web.dto.LoginFormDataDto;
import org.springframework.stereotype.Component;

@Component
public class LoginFormDataConverter {
    public LoginFormData convertToEntity(LoginFormDataDto dto) {
        LoginFormData loginFormData = new LoginFormData();
        loginFormData.setLogin(dto.getLogin());
        loginFormData.setPassword(dto.getPassword());
        loginFormData.setEmail(dto.getEmail());
        loginFormData.setFio(dto.getFio());
        return loginFormData;
    }

    public LoginFormDataDto convertToDto(LoginFormData data) {
        return LoginFormDataDto.builder()
                .login(data.getLogin())
                .password(data.getPassword())
                .email(data.getEmail())
                .fio(data.getFio())
                .build();
    }
}
