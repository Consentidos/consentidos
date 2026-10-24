package com.veterinaria.consentidos.features.auth.infrastructure.persistence.adapter;

import com.veterinaria.consentidos.features.auth.domain.entity.LoginAttemptPolicy;
import com.veterinaria.consentidos.features.auth.domain.entity.LoginAttemptState;
import com.veterinaria.consentidos.features.auth.domain.repository.LoginAttemptRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
@Profile("local & !external-storage")
public class InMemoryLoginAttemptRepositoryAdapter extends AbstractLoginAttemptRepositoryAdapter implements LoginAttemptRepository {

    private final ConcurrentMap<String, LoginAttemptState> attempts = new ConcurrentHashMap<>();

    @Override
    public Optional<LoginAttemptState> findByKey(String key) {
        LoginAttemptState state = attempts.get(key);
        if (state == null) {
            return Optional.empty();
        }

        if (state.isExpired(Instant.now())) {
            attempts.remove(key);
            return Optional.empty();
        }

        return Optional.of(state);
    }

    @Override
    public void registerFailure(
        String key,
        String userHint,
        String deviceId,
        String clientIp,
        String userAgent,
        LoginAttemptPolicy policy
    ) {
        Instant now = Instant.now();
        attempts.compute(key, (currentKey, current) -> {
            Instant windowStartedAt = current == null || current.isExpired(now) ? now : current.windowStartedAt();
            int count = current == null || current.isExpired(now) ? 1 : current.attempts() + 1;
            Instant blockedUntil = count >= policy.maxAttempts() ? now.plus(policy.lockout()) : null;
            Instant expiresAt = blockedUntil != null ? blockedUntil : windowStartedAt.plus(policy.window());

            return buildState(new LoginAttemptBuildInput(
                currentKey,
                userHint,
                deviceId,
                clientIp,
                userAgent,
                count,
                windowStartedAt,
                now,
                blockedUntil,
                expiresAt
            ));
        });
    }

    @Override
    public void clear(String key) {
        attempts.remove(key);
    }

}
