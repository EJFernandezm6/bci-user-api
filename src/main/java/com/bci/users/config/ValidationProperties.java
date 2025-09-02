package com.bci.users.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.validation")
@Data
public class ValidationProperties {

    private String emailRegex;

    private String passwordRegex;
}