package com.veterinaria.consentidos.features.auth.infrastructure.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Objects;

@Getter
@Validated
@ConfigurationProperties(prefix = "auth.google")
public class GoogleAuthProperties {

    @NotEmpty(message = "auth.google.client-ids must contain at least one value")
    private final List<@NotBlank(message = "Google client id must not be blank") String> clientIds;

    private final List<String> allowedDomains;

    public GoogleAuthProperties(List<String> clientIds, List<String> allowedDomains) {
        this.clientIds = Objects.requireNonNullElse(clientIds, List.of());
        this.allowedDomains = Objects.requireNonNullElse(allowedDomains, List.of());
    }
}
