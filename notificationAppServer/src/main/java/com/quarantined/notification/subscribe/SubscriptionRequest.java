package com.quarantined.notification.subscribe;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class SubscriptionRequest {

	@NotNull
	private Long channelId;
}
