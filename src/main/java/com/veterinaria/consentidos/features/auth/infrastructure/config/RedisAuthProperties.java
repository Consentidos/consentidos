package com.veterinaria.consentidos.features.auth.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import lombok.Getter;
import lombok.Setter;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "auth.redis")
public class RedisAuthProperties {

    private String host;
    private int port = 6379;
    private String username = "default";
    private String password;
    private boolean ssl = true;
    private int database = 0;
    private long timeoutSeconds = 10;
    private String keyPrefix = "consentidos:auth";
}

