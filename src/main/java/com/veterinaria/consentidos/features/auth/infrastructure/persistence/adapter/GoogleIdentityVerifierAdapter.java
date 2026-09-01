package com.veterinaria.consentidos.features.auth.infrastructure.persistence.adapter;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.veterinaria.consentidos.features.auth.domain.entity.GoogleIdentity;
import com.veterinaria.consentidos.features.auth.domain.repository.GoogleIdentityVerifier;
import com.veterinaria.consentidos.features.auth.infrastructure.config.GoogleAuthProperties;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;

@Component
public class GoogleIdentityVerifierAdapter implements GoogleIdentityVerifier {

    private final GoogleIdTokenVerifier verifier;

    public GoogleIdentityVerifierAdapter(GoogleAuthProperties properties) {
        this.verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance())
            .setAudience(properties.getClientIds())
            .build();
    }

    @Override
    public GoogleIdentity verifyIdToken(String idToken) {
        try {
            GoogleIdToken token = verifier.verify(idToken);
            if (token == null) {
                throw new SecurityException("Invalid Google id_token");
            }

            GoogleIdToken.Payload payload = token.getPayload();

            // TODO: Optional tenant/domain restriction can be added here using payload.getHostedDomain().

            return new GoogleIdentity(
                payload.getSubject(),
                payload.getEmail(),
                Boolean.TRUE.equals(payload.getEmailVerified()),
                (String) payload.get("name"),
                (String) payload.get("picture"),
                payload.getIssuer(),
                payload.getAudienceAsList() != null && !payload.getAudienceAsList().isEmpty()
                    ? payload.getAudienceAsList().get(0)
                    : String.valueOf(payload.getAudience())
            );
        } catch (GeneralSecurityException | IOException ex) {
            throw new SecurityException("Failed to verify Google id_token", ex);
        }
    }
}


