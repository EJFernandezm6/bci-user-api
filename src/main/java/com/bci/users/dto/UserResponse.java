package com.bci.users.dto;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class UserResponse {
    private UUID id;
    private Instant created;
    private Instant modified;
    private Instant lastLogin;
    private String token;
    private boolean isActive;
}
