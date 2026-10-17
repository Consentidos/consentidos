package com.veterinaria.consentidos.features.auth.domain.entity;

import java.time.Duration;

public record LoginAttemptPolicy(
	int maxAttempts,
	Duration window,
	Duration lockout
) {
}

