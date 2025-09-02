package com.bci.users.service;

import com.bci.users.dto.UserRequest;
import com.bci.users.dto.UserResponse;
import org.springframework.security.core.Authentication;

import java.util.UUID;

public interface UserService {
    UserResponse register(UserRequest req);

    UserResponse getById(UUID id, Authentication auth);
}
