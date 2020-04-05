package com.quarantined.notification.topic;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
class ChannelRequest {
    
	@NotBlank
    @Size(max = 40)
    private String name;
}
