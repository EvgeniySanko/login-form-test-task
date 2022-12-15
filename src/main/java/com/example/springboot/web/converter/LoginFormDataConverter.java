package com.example.springboot.web.converter;

import com.example.springboot.db.entity.LoginFormData;
import com.example.springboot.web.dto.LoginFormDataDto;
import org.springframework.stereotype.Component;

@Component
public class LoginFormDataConverter {
    public LoginFormData convertToEntity(LoginFormDataDto dto) {
        return LoginFormData.builder()
                .login(dto.getLogin())
                .password(dto.getPassword())
                .email(dto.getEmail())
                .fio(dto.getFio())
                .build();
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
