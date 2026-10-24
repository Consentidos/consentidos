package com.veterinaria.consentidos.features.auth.domain.repository;

import com.veterinaria.consentidos.features.auth.domain.entity.GoogleIdentity;

public interface GoogleIdentityVerifier {
    GoogleIdentity verifyIdToken(String idToken);
}

