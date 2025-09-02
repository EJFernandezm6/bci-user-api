package com.bci.users.controller;

import com.bci.users.dto.UserRequest;
import com.bci.users.dto.UserResponse;
import com.bci.users.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService service;
    public UserController(UserService service){ this.service = service; }

    @Operation(summary = "Registrar un nuevo usuario")
    @ApiResponse(
            responseCode = "201",
            description = "Usuario creado exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = UserResponse.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "Solicitud inválida",
            content = @Content(mediaType = "application/json")
    )
    @ApiResponse(
            responseCode = "500",
            description = "Error no controlado",
            content = @Content(mediaType = "application/json")
    )
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponse> register(@Valid @RequestBody UserRequest request){
        UserResponse response = service.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserResponse getById(@PathVariable UUID id, Authentication auth) {
        return service.getById(id, auth);
    }
}
