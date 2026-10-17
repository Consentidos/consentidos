package com.veterinaria.consentidos.features.auth.infrastructure.persistence.adapter;

import com.veterinaria.consentidos.features.auth.domain.entity.AuthUser;
import com.veterinaria.consentidos.features.auth.domain.repository.AuthUserRepository;
import com.veterinaria.consentidos.features.auth.infrastructure.config.PreRegisteredUsersProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Profile("local & !external-storage")
public class InMemoryAuthUserRepositoryAdapter implements AuthUserRepository {

    private final Map<String, AuthUser> byGoogleSub = new ConcurrentHashMap<>();
    private final Map<String, AuthUser> byUserId = new ConcurrentHashMap<>();

    public InMemoryAuthUserRepositoryAdapter(PreRegisteredUsersProperties properties) {
        properties.getPreRegistered().forEach(user -> {
            String userId = UUID.nameUUIDFromBytes(("auth-user:" + user.getGoogleSub()).getBytes(StandardCharsets.UTF_8)).toString();
            AuthUser authUser = new AuthUser(
                userId,
                user.getGoogleSub(),
                user.getEmail(),
                user.getDisplayName(),
                user.isActive()
            );

            byGoogleSub.put(authUser.googleSub(), authUser);
            byUserId.put(authUser.userId(), authUser);
        });
    }

    @Override
    public Optional<AuthUser> findActiveByGoogleSub(String googleSub) {
        AuthUser user = byGoogleSub.get(googleSub);
        if (user == null || !user.active()) {
            return Optional.empty();
        }
        return Optional.of(user);
    }

    @Override
    public Optional<AuthUser> findActiveByUserId(String userId) {
        AuthUser user = byUserId.get(userId);
        if (user == null || !user.active()) {
            return Optional.empty();
        }
        return Optional.of(user);
    }
}

