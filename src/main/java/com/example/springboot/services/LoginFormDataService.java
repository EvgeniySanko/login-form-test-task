package com.example.springboot.services;

import com.example.springboot.db.entity.LoginFormData;

public interface LoginFormDataService {
    LoginFormData save(LoginFormData data);
    LoginFormData findById(Long id);
}
