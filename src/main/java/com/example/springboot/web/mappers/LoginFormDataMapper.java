package com.example.springboot.web.mappers;

import com.example.springboot.db.entity.LoginFormData;
import com.example.springboot.web.dto.LoginFormDataDto;
import org.mapstruct.Mapper;

/**
 * Маппер объектов
 * dto -> entity
 * entity -> dto
 */
@Mapper(componentModel = "spring")
public interface LoginFormDataMapper {
    LoginFormData dtoToEntity(LoginFormDataDto dto);
    LoginFormDataDto entityToDto(LoginFormData entity);
}
