package com.bci.users.service.impl;

import com.bci.users.dto.UserRequest;
import com.bci.users.dto.UserResponse;
import com.bci.users.entity.PhoneEntity;
import com.bci.users.entity.UserEntity;
import com.bci.users.exception.BusinessException;
import com.bci.users.mapper.UserMapper;
import com.bci.users.repository.UserRepository;
import com.bci.users.service.JwtService;
import com.bci.users.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper mapper;

    public UserServiceImpl(UserRepository userRepository,
                           JwtService jwtService,
                           PasswordEncoder passwordEncoder,
                           UserMapper mapper) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public UserResponse register(UserRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new BusinessException(HttpStatus.CONFLICT, "El correo ya fué registrado");
        }

        UserEntity entity = mapper.toEntity(req);
        entity.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        if (entity.getPhones() != null) {
            for (PhoneEntity ph : entity.getPhones()) {
                ph.setUser(entity);
            }
        }
        String jwt = jwtService.generateToken(entity.getEmail());
        entity.setToken(jwt);
        entity.setLastLogin(Instant.now());

        userRepository.save(entity);

        return mapper.toResponse(entity);
    }

    public UserResponse getById(UUID id, Authentication auth) {
        if (auth == null || auth.getName() == null) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "No autorizado");
        }

        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        if (!user.getEmail().equals(auth.getName())) {
            throw new BusinessException(HttpStatus.FORBIDDEN, "Acceso denegado");
        }

        return mapper.toResponse(user);
    }


}
