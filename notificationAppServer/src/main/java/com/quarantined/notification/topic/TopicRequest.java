package com.quarantined.notification.topic;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class TopicRequest {

	@NotBlank
	@Size(max = 140)
	private String name;

	@NotNull
	@Size(min = 2, max = 6)
	@Valid
	private List<ChannelRequest> channels;

	@NotNull
	@Valid
	private TopicLength topicLength;
}
