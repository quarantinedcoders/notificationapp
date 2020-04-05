package com.quarantined.notification.user;

import java.time.Instant;

import lombok.Data;

@Data
class UserProfile {
	private final long id;
	private final String username;
	private final String name;
	private final Instant joinedAt;
	private final long topicCount;
	private final long subscriptionCount;
}
