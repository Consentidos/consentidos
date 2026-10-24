package com.veterinaria.consentidos.features.auth.infrastructure.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@Getter
@ConfigurationProperties(prefix = "auth.redis")
public class RedisAuthProperties {

    private final String host;
    private final int port;
    private final String username;
    private final String password;
    private final boolean ssl;
    private final int database;
    private final long timeoutSeconds;
    private final String keyPrefix;

    public RedisAuthProperties(
        @DefaultValue("") String host,
        @DefaultValue("6379") int port,
        @DefaultValue("default") String username,
        @DefaultValue("") String password,
        @DefaultValue("true") boolean ssl,
        @DefaultValue("0") int database,
        @DefaultValue("10") long timeoutSeconds,
        @DefaultValue("consentidos:auth") String keyPrefix
    ) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.ssl = ssl;
        this.database = database;
        this.timeoutSeconds = timeoutSeconds;
        this.keyPrefix = keyPrefix;
    }
}
