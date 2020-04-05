package com.quarantined.notification.topic;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class TopicLength {

	@NotNull
	@Max(7)
	private Integer days;

	@NotNull
	@Max(23)
	private Integer hours;
}
