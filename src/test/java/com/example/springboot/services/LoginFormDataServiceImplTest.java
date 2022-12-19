package com.example.springboot.services;

import com.example.springboot.db.entity.LoginFormData;
import com.example.springboot.db.repository.LoginFormDataRepository;
import com.example.springboot.exception.DataNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class LoginFormDataServiceImplTest {

    @InjectMocks
    private LoginFormDataServiceImpl loginFormDataService;

    @Mock
    private LoginFormDataRepository loginFormDataRepository;

    @Captor
    private ArgumentCaptor<LoginFormData> loginFormDataCaptor;

    @Test
    void saveTest() {
        LoginFormData loginFormData = getLoginFormData();
        when(loginFormDataRepository.save(loginFormDataCaptor.capture())).thenReturn(loginFormData);

        loginFormDataService.save(loginFormData);

        LoginFormData captorValue = loginFormDataCaptor.getValue();
        assertEquals("login", captorValue.getLogin());
        assertEquals("email", captorValue.getEmail());
        assertEquals("fio", captorValue.getFio());
        assertEquals("password", captorValue.getPassword());
    }

    @Test
    void findByIdTest() {
        LoginFormData loginFormData = getLoginFormData();
        when(loginFormDataRepository.findById(any())).thenReturn(Optional.of(loginFormData));

        LoginFormData found = loginFormDataService.findById(1L);
        assertEquals("login", found.getLogin());
        assertEquals("email", found.getEmail());
        assertEquals("fio", found.getFio());
        assertEquals("password", found.getPassword());
    }

    @Test
    void findByIdTest_shouldThrowDataNotFoundException() {
        when(loginFormDataRepository.findById(any())).thenReturn(Optional.empty());

        DataNotFoundException dataNotFoundException = assertThrows(DataNotFoundException.class, () -> loginFormDataService.findById(1L));
        assertEquals("Data with id = 1 not found.", dataNotFoundException.getMessage());
    }

    private static LoginFormData getLoginFormData() {
        LoginFormData loginFormData = new LoginFormData();
        loginFormData.setLogin("login");
        loginFormData.setPassword("password");
        loginFormData.setEmail("email");
        loginFormData.setFio("fio");
        return loginFormData;
    }
}