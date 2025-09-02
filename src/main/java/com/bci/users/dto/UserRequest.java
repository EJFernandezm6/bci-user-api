package com.bci.users.dto;

import com.bci.users.validation.EmailRegex;
import com.bci.users.validation.PasswordRegex;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class UserRequest {
    @NotBlank
    private String name;

    @NotBlank
    @EmailRegex
    private String email;

    @NotBlank
    @PasswordRegex
    private String password;

    @NotNull
    @Size(min=1)
    private List<PhoneDto> phones;
}
