package com.veterinaria.consentidos.features.auth.infrastructure.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;

@Component
@Data
@Validated
@ConfigurationProperties(prefix = "auth.google")
public class GoogleAuthProperties {

    @NotEmpty(message = "auth.google.client-ids must contain at least one value")
    private List<@NotBlank(message = "Google client id must not be blank") String> clientIds = new ArrayList<>();

    private List<String> allowedDomains = new ArrayList<>();
}
