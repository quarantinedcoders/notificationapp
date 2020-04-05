package com.quarantined.notification.subscribe;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.quarantined.notification.exception.BadRequestException;
import com.quarantined.notification.exception.ResourceNotFoundException;
import com.quarantined.notification.security.UserPrincipal;
import com.quarantined.notification.topic.Channel;
import com.quarantined.notification.topic.ModelMapper;
import com.quarantined.notification.topic.Topic;
import com.quarantined.notification.topic.TopicRepository;
import com.quarantined.notification.topic.TopicResponse;
import com.quarantined.notification.user.User;
import com.quarantined.notification.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SubscriptionService {

	private final TopicRepository notificationTopicRepository;

	private final SubscriptionRepository subscriptionRepository;

	private final UserRepository userRepository;

	private static final Logger logger = LoggerFactory.getLogger(SubscriptionService.class);

	public TopicResponse subscribeAndGetUpdatedTopic(Long topicId, SubscriptionRequest subscriptionRequest,
			UserPrincipal currentUser) {
		Topic topic = notificationTopicRepository.findById(topicId)
				.orElseThrow(() -> new ResourceNotFoundException("Topic", "id", topicId));

		if (topic.getExpirationDateTime().isBefore(Instant.now())) {
			throw new BadRequestException("Sorry! This Topic has already expired");
		}

		User user = userRepository.getOne(currentUser.getId());

		Channel selectedChannel = topic.getChannels().stream()
				.filter(choice -> choice.getId().equals(subscriptionRequest.getChannelId())).findFirst()
				.orElseThrow(() -> new ResourceNotFoundException("Channel", "id", subscriptionRequest.getChannelId()));

		Subscription subscription = new Subscription();
		subscription.setTopic(topic);
		subscription.setUser(user);
		subscription.setChannel(selectedChannel);

		try {
			subscription = subscriptionRepository.save(subscription);
		} catch (DataIntegrityViolationException ex) {
			logger.info("User {} has already subscribed in Topic {}", currentUser.getId(), topicId);
			throw new BadRequestException("Sorry! You already have subscription for this topic");
		}

		// -- Subscription Saved, Return the updated Topic Response now --

		// Retrieve Subscription Counts of every channel belonging to the current Topic
		List<ChannelSubscriptionCount> subscriptions = subscriptionRepository.countByTopicIdGroupByChannelId(topicId);

		Map<Long, Long> channelSubscriptionMap = subscriptions.stream().collect(Collectors
				.toMap(ChannelSubscriptionCount::getChannelId, ChannelSubscriptionCount::getSubscriptionCount));

		// Retrieve topic creator details
		User creator = userRepository.findById(topic.getCreatedBy())
				.orElseThrow(() -> new ResourceNotFoundException("User", "id", topic.getCreatedBy()));

		return ModelMapper.mapTopicToTopicResponse(topic, channelSubscriptionMap, creator,
				subscription.getChannel().getId());
	}

}
