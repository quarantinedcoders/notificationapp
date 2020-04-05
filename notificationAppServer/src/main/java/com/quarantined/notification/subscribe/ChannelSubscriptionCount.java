package com.quarantined.notification.subscribe;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ChannelSubscriptionCount {
	private final Long channelId;
	private final Long subscriptionCount;
}
