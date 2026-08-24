package com.veterinaria.consentidos.features.auth.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.veterinaria.consentidos.features.auth.application.command.LoginWithGoogleCommand;
import com.veterinaria.consentidos.features.auth.application.command.LogoutCommand;
import com.veterinaria.consentidos.features.auth.application.command.RefreshSessionCommand;
import com.veterinaria.consentidos.features.auth.application.usecase.LoginWithGoogleUseCase;
import com.veterinaria.consentidos.features.auth.application.usecase.LogoutUseCase;
import com.veterinaria.consentidos.features.auth.application.usecase.RefreshSessionUseCase;
import com.veterinaria.consentidos.features.auth.domain.entity.TokenPair;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LoginWithGoogleUseCase loginWithGoogleUseCase;

    @MockBean
    private RefreshSessionUseCase refreshSessionUseCase;

    @MockBean
    private LogoutUseCase logoutUseCase;

    @Test
    void loginShouldReturnTokenPayload() throws Exception {
        TokenPair pair = new TokenPair(
            "access",
            "refresh",
            Instant.now().plusSeconds(600),
            Instant.now().plusSeconds(3600),
            "Bearer",
            "session-1"
        );
        when(loginWithGoogleUseCase.execute(any(LoginWithGoogleCommand.class))).thenReturn(pair);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new LoginBody("token"))))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("access")));

        verify(loginWithGoogleUseCase).execute(any(LoginWithGoogleCommand.class));
    }

    @Test
    void refreshShouldReturnTokenPayload() throws Exception {
        TokenPair pair = new TokenPair(
            "new-access",
            "new-refresh",
            Instant.now().plusSeconds(600),
            Instant.now().plusSeconds(3600),
            "Bearer",
            "session-2"
        );
        when(refreshSessionUseCase.execute(any(RefreshSessionCommand.class))).thenReturn(pair);

        mockMvc.perform(post("/api/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new RefreshBody("refresh-token"))))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("new-access")));

        verify(refreshSessionUseCase).execute(any(RefreshSessionCommand.class));
    }

    @Test
    void logoutShouldReturnSuccessMessage() throws Exception {
        mockMvc.perform(post("/api/auth/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new RefreshBody("refresh-token"))))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Session closed successfully")));

        verify(logoutUseCase).execute(any(LogoutCommand.class));
    }

    private record LoginBody(String idToken) {
    }

    private record RefreshBody(String refreshToken) {
    }
}

