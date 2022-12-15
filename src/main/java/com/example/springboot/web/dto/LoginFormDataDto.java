package com.example.springboot.web.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginFormDataDto {
    private String login;
    private String password;
    private String email;
    private String fio;
}
