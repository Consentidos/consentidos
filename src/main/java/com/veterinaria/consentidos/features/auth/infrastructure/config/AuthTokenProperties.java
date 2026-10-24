package com.veterinaria.consentidos.features.auth.infrastructure.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@Getter
@ConfigurationProperties(prefix = "auth.token")
public class AuthTokenProperties {

    private final String issuer;
    private final String audience;
    private final long accessTtlSeconds;
    private final long refreshTtlSeconds;
    private final String privateKeyPem;
    private final String publicKeyPem;

    public AuthTokenProperties(
        @DefaultValue("consentidos-auth") String issuer,
        @DefaultValue("consentidos-web") String audience,
        @DefaultValue("900") long accessTtlSeconds,
        @DefaultValue("604800") long refreshTtlSeconds,
        @DefaultValue("") String privateKeyPem,
        @DefaultValue("") String publicKeyPem
    ) {
        this.issuer = issuer;
        this.audience = audience;
        this.accessTtlSeconds = accessTtlSeconds;
        this.refreshTtlSeconds = refreshTtlSeconds;
        this.privateKeyPem = privateKeyPem;
        this.publicKeyPem = publicKeyPem;
    }
}
