package com.quarantined.notification.topic;

import lombok.Data;

@Data
class ChannelResponse {
	private long id;
	private String name;
	private long subscriptionCount;
}
