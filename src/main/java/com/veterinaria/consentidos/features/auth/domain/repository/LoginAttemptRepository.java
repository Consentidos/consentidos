package com.veterinaria.consentidos.features.auth.domain.repository;

import com.veterinaria.consentidos.features.auth.domain.entity.LoginAttemptPolicy;
import com.veterinaria.consentidos.features.auth.domain.entity.LoginAttemptState;

import java.util.Optional;

public interface LoginAttemptRepository {
	Optional<LoginAttemptState> findByKey(String key);

	void registerFailure(
		String key,
		String userHint,
		String deviceId,
		String clientIp,
		String userAgent,
		LoginAttemptPolicy policy
	);

	void clear(String key);
}

