package com.example.springboot.db.repository;

import com.example.springboot.db.entity.LoginFormData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoginFormDataRepository extends JpaRepository<LoginFormData, Long> {
}
