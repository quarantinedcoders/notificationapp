package com.quarantined.notification.user;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
class UserIdentityAvailability {
	private final Boolean available;
}
