package com.veterinaria.consentidos.features.auth.infrastructure.config;

import com.veterinaria.consentidos.features.auth.domain.entity.LoginAttemptPolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class LoginAttemptPolicyConfiguration {

    @Bean
    public LoginAttemptPolicy loginAttemptPolicy(AuthLoginAttemptProperties properties) {
        return new LoginAttemptPolicy(
            properties.getMaxAttempts(),
            Duration.ofSeconds(properties.getWindowSeconds()),
            Duration.ofSeconds(properties.getLockoutSeconds())
        );
    }
}

