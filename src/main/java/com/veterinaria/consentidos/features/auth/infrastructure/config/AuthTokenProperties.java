package com.veterinaria.consentidos.features.auth.infrastructure.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "auth.token")
public class AuthTokenProperties {

    private String issuer = "consentidos-auth";
    private String audience = "consentidos-web";
    private long accessTtlSeconds = 900;
    private long refreshTtlSeconds = 604800;
    private String privateKeyPem;
    private String publicKeyPem;

}

