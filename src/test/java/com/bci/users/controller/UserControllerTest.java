package com.bci.users.controller;

import com.bci.users.config.ValidationProperties;
import com.bci.users.exception.GlobalExceptionHandler;
import com.bci.users.dto.UserRequest;
import com.bci.users.dto.UserResponse;
import com.bci.users.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class UserControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private UserService userService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        ValidationProperties validationProperties() {
            ValidationProperties p = new ValidationProperties();
            p.setEmailRegex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.cl$");
            p.setPasswordRegex("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)[A-Za-z\\d@$!%*?&]{8,}$");
            return p;
        }
    }

    @Test
    void registerAndReturn201WithBody() throws Exception {
        UserResponse resp = new UserResponse();
        resp.setId(UUID.randomUUID());
        resp.setCreated(Instant.now());
        resp.setModified(Instant.now());
        resp.setLastLogin(Instant.now());
        resp.setToken("JWT_TOKEN");
        resp.setActive(true);

        when(userService.register(any(UserRequest.class))).thenReturn(resp);

        String body = """
            {"name":"Edu Fernandez","email":"edu@dominio.cl","password":"Bci2025123",
             "phones":[{"number":"1234567","cityCode":"1","contryCode":"57"}]}
            """;

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.token").value("JWT_TOKEN"))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void registerWithInvalidValidationAndReturn400() throws Exception {
        String badBody = """
            {"name":"","email":"edu@dominio.cl","password":"abc",
             "phones":[{"number":"1234567","cityCode":"1","contryCode":"57"}]}
            """;

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(badBody))
                .andExpect(status().isBadRequest());
    }
}