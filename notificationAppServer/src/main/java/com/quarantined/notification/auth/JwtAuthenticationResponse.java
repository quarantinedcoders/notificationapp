package com.quarantined.notification.auth;

import lombok.Data;

@Data
public class JwtAuthenticationResponse {

	private final String accessToken;
	private String tokenType = "Bearer";
}
