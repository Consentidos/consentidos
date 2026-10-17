package com.veterinaria.consentidos.features.auth.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Data
@ConfigurationProperties(prefix = "auth.google")
public class GoogleAuthProperties {

    private List<String> clientIds = new ArrayList<>();
    private List<String> allowedDomains = new ArrayList<>();
}

