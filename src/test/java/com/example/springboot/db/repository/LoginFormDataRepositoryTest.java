package com.example.springboot.db.repository;

import com.example.springboot.db.entity.LoginFormData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class LoginFormDataRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private LoginFormDataRepository loginFormDataRepository;

    @Test
    public void saveTest() {
        LoginFormData loginFormData = getLoginFormData();
        List<LoginFormData> empty = loginFormDataRepository.findAll();
        assertTrue(empty.isEmpty());

        LoginFormData savedLoginFormData = loginFormDataRepository.save(loginFormData);
        List<LoginFormData> found = loginFormDataRepository.findAll();

        assertEquals(savedLoginFormData, found.get(0));
    }

    @Test
    public void findByIdTest() {
        LoginFormData loginFormData = getLoginFormData();

        Optional<LoginFormData> empty = loginFormDataRepository.findById(1L);
        assertTrue(empty.isEmpty());

        Long id = entityManager.persistAndGetId(loginFormData, Long.class);
        entityManager.flush();

        Optional<LoginFormData> found = loginFormDataRepository.findById(id);
        assertFalse(found.isEmpty());
        assertEquals(id, found.get().getId());
    }

    private static LoginFormData getLoginFormData() {
        return LoginFormData.builder()
                .login("login")
                .email("email")
                .fio("fio")
                .password("password")
                .build();
    }
}