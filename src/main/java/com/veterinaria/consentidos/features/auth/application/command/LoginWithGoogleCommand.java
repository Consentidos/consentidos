package com.veterinaria.consentidos.features.auth.application.command;

public record LoginWithGoogleCommand(
	String idToken,
	String deviceId,
	String clientIp,
	String userAgent
) {
}

