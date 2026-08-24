package com.veterinaria.consentidos.features.auth.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "auth.users")
public class PreRegisteredUsersProperties {

    private List<PreRegisteredUser> preRegistered = new ArrayList<>();

    public List<PreRegisteredUser> getPreRegistered() {
        return preRegistered;
    }

    public void setPreRegistered(List<PreRegisteredUser> preRegistered) {
        this.preRegistered = preRegistered;
    }

    public static class PreRegisteredUser {
        private String googleSub;
        private String email;
        private String displayName;
        private boolean active = true;

        public String getGoogleSub() {
            return googleSub;
        }

        public void setGoogleSub(String googleSub) {
            this.googleSub = googleSub;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }
    }
}

