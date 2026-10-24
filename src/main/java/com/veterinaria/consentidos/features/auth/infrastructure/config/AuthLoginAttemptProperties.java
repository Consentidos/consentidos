package com.veterinaria.consentidos.features.auth.infrastructure.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@Getter
@ConfigurationProperties(prefix = "auth.login-attempts")
public class AuthLoginAttemptProperties {

    private final int maxAttempts;
    private final long windowSeconds;
    private final long lockoutSeconds;

    public AuthLoginAttemptProperties(
        @DefaultValue("3") int maxAttempts,
        @DefaultValue("120") long windowSeconds,
        @DefaultValue("900") long lockoutSeconds
    ) {
        this.maxAttempts = maxAttempts;
        this.windowSeconds = windowSeconds;
        this.lockoutSeconds = lockoutSeconds;
    }
}
