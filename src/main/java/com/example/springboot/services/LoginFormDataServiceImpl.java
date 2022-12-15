package com.example.springboot.services;

import com.example.springboot.db.entity.LoginFormData;
import com.example.springboot.db.repository.LoginFormDataRepository;
import com.example.springboot.exception.DataNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class LoginFormDataServiceImpl implements LoginFormDataService{
    private final LoginFormDataRepository loginFormDataRepository;

    @Override
    public LoginFormData save(LoginFormData data) {
        return loginFormDataRepository.save(data);
    }

    @Override
    @Transactional(readOnly = true)
    public LoginFormData findById(Long id) {
        return loginFormDataRepository.findById(id).orElseThrow(() -> new DataNotFoundException(id));
    }
}
