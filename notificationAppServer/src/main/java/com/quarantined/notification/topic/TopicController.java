package com.quarantined.notification.topic;

import java.net.URI;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.quarantined.notification.common.ApiResponse;
import com.quarantined.notification.common.AppConstants;
import com.quarantined.notification.security.CurrentUser;
import com.quarantined.notification.security.UserPrincipal;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RestController
@RequestMapping("/api/topic")
public class TopicController {

	private final TopicService topicService;

	private static final Logger logger = LoggerFactory.getLogger(TopicController.class);

	@GetMapping
	public PagedResponse<TopicResponse> getTopic(@CurrentUser UserPrincipal currentUser,
			@RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
			@RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) {
		return topicService.getAllTopics(currentUser, page, size);
	}

	@PostMapping
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<?> createTopic(@Valid @RequestBody TopicRequest topicRequest) {
		Topic topic = topicService.createTopic(topicRequest);

		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{topicId}").buildAndExpand(topic.getId())
				.toUri();

		return ResponseEntity.created(location).body(new ApiResponse(true, "Topic Created Successfully"));
	}

	@GetMapping("/{topicId}")
	public TopicResponse getTopicById(@CurrentUser UserPrincipal currentUser, @PathVariable Long topicId) {
		return topicService.getTopicById(topicId, currentUser);
	}
	
	@GetMapping("/user/{username}/topics")
    public PagedResponse<TopicResponse> getTopicsCreatedBy(@PathVariable(value = "username") String username,
                                                         @CurrentUser UserPrincipal currentUser,
                                                         @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
                                                         @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) {
        return topicService.getTopicsCreatedBy(username, currentUser, page, size);
    }


    @GetMapping("/user/{username}/subscriptions")
    public PagedResponse<TopicResponse> getTopicsSubscribedBy(@PathVariable(value = "username") String username,
                                                       @CurrentUser UserPrincipal currentUser,
                                                       @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
                                                       @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) {
        return topicService.getTopicsSubscribedBy(username, currentUser, page, size);
    }
}
