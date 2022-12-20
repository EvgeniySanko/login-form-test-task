package com.example.springboot.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginFormDataDto {
    private String login;
    private String password;
    private String email;
    private String fio;
}
