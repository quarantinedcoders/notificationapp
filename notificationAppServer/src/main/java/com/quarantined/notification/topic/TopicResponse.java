package com.quarantined.notification.topic;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.quarantined.notification.user.UserSummary;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TopicResponse {
	
	private Long id;
	private String name;
	private List<ChannelResponse> channels;
	private UserSummary createdBy;
	private Instant creationDateTime;
	private Instant expirationDateTime;
	private Boolean isExpired;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Long selectedChannel;
	private Long totalSubscriptions;
}
