package com.bci.users.service.impl;

import com.bci.users.dto.PhoneDto;
import com.bci.users.dto.UserRequest;
import com.bci.users.dto.UserResponse;
import com.bci.users.entity.PhoneEntity;
import com.bci.users.entity.UserEntity;
import com.bci.users.exception.BusinessException;
import com.bci.users.mapper.UserMapper;
import com.bci.users.repository.UserRepository;
import com.bci.users.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private JwtService jwtService;
    @Mock private PasswordEncoder passwordEncoder;

    private UserMapper mapper;

    @InjectMocks
    private UserServiceImpl service;

    @BeforeEach
    void setUp() {
        mapper = new UserMapper();
        service = new UserServiceImpl(userRepository, jwtService, passwordEncoder, mapper);
    }

    @Test
    void registerUserAndReturnResponseWithToken() {
        // given
        UserRequest req = new UserRequest();
        req.setName("Edu Fernandez");
        req.setEmail("edu@fernandez2.com");
        req.setPassword("Bci2025123");
        PhoneDto p = new PhoneDto();
        p.setNumber("1234567");
        p.setCityCode("1");
        p.setContryCode("57");
        req.setPhones(List.of(p));

        when(userRepository.existsByEmail(req.getEmail())).thenReturn(false);
        when(passwordEncoder.encode("Bci2025123")).thenReturn("ENCODED_PWD");
        when(jwtService.generateToken(req.getEmail())).thenReturn("JWT_TOKEN");

        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        when(userRepository.save(captor.capture())).thenAnswer(inv -> {
            UserEntity e = inv.getArgument(0);
            e.setId(java.util.UUID.randomUUID());
            if (e.getCreated() == null) {
                java.time.Instant now = java.time.Instant.now();
                e.setCreated(now);
                e.setModified(now);
                e.setActive(true);
            }
            return e;
        });

        UserResponse res = service.register(req);

        UserEntity saved = captor.getValue();
        assertThat(saved.getPasswordHash()).isEqualTo("ENCODED_PWD");
        assertThat(saved.getToken()).isEqualTo("JWT_TOKEN");
        assertThat(saved.getPhones()).hasSize(1);
        PhoneEntity savedPhone = saved.getPhones().get(0);
        assertThat(savedPhone.getUser()).isNotNull();

        assertThat(res.getId()).isNotNull();
        assertThat(res.getToken()).isEqualTo("JWT_TOKEN");
        assertThat(res.isActive()).isTrue();
        assertThat(res.getCreated()).isNotNull();
        assertThat(res.getModified()).isNotNull();
        assertThat(res.getLastLogin()).isNotNull();

        verify(userRepository).existsByEmail(req.getEmail());
        verify(passwordEncoder).encode("Bci2025123");
        verify(jwtService).generateToken(req.getEmail());
        verify(userRepository).save(any(UserEntity.class));
        verifyNoMoreInteractions(userRepository, jwtService, passwordEncoder);
    }

    @Test
    void registerDuplicateEmailThrowBusinessException409() {
        // given
        UserRequest req = new UserRequest();
        req.setName("Edu");
        req.setEmail("edu@fernandez2.com");
        req.setPassword("Bci2025123");
        req.setPhones(List.of(new PhoneDto()));

        when(userRepository.existsByEmail(req.getEmail())).thenReturn(true);

        // when / then
        assertThatThrownBy(() -> service.register(req))
                .isInstanceOf(BusinessException.class)
                .hasMessage("El correo ya fué registrado")
                .satisfies(ex -> {
                    BusinessException be = (BusinessException) ex;
                    assertThat(be.getStatus()).isEqualTo(HttpStatus.CONFLICT);
                });

        verify(userRepository).existsByEmail(req.getEmail());
        verifyNoMoreInteractions(userRepository, jwtService, passwordEncoder);
    }
}