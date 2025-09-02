package com.bci.users.mapper;

import com.bci.users.dto.PhoneDto;
import com.bci.users.dto.UserRequest;
import com.bci.users.dto.UserResponse;
import com.bci.users.entity.PhoneEntity;
import com.bci.users.entity.UserEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    public UserEntity toEntity(UserRequest req) {
        UserEntity entity = new UserEntity();
        entity.setName(req.getName());
        entity.setEmail(req.getEmail());
        List<PhoneEntity> phones = req.getPhones().stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
        entity.setPhones(phones);
        return entity;
    }

    public PhoneEntity toEntity(PhoneDto dto) {
        PhoneEntity entity = new PhoneEntity();
        entity.setNumber(dto.getNumber());
        entity.setCityCode(dto.getCityCode());
        entity.setContryCode(dto.getContryCode());
        return entity;
    }

    public UserResponse toResponse(UserEntity entity) {
        UserResponse res = new UserResponse();
        res.setId(entity.getId());
        res.setCreated(entity.getCreated());
        res.setModified(entity.getModified());
        res.setLastLogin(entity.getLastLogin());
        res.setToken(entity.getToken());
        res.setActive(entity.isActive());
        return res;
    }
}
