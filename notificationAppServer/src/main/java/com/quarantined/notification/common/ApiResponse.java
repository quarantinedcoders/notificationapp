package com.quarantined.notification.common;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ApiResponse {
	private final Boolean success;
	private final String message;
}
