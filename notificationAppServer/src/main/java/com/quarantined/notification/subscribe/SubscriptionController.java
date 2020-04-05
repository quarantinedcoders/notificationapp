package com.quarantined.notification.subscribe;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.quarantined.notification.security.CurrentUser;
import com.quarantined.notification.security.UserPrincipal;
import com.quarantined.notification.topic.TopicResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RestController
@RequestMapping("/api/subscription")
public class SubscriptionController {

	private final SubscriptionService subscriptionService;

	@PostMapping("/{topicId}/subscribe")
	@PreAuthorize("hasRole('USER')")
	public TopicResponse subscribe(@CurrentUser UserPrincipal currentUser, @PathVariable Long topicId,
			@Valid @RequestBody SubscriptionRequest subscriptionRequest) {
		return subscriptionService.subscribeAndGetUpdatedTopic(topicId, subscriptionRequest, currentUser);
	}

}
