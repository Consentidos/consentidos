package com.veterinaria.consentidos.features.auth.infrastructure.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "auth.login-attempts")
public class AuthLoginAttemptProperties {

    private int maxAttempts = 3;
    private long windowSeconds = 120;
    private long lockoutSeconds = 900;

}

