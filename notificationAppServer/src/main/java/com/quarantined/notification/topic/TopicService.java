package com.quarantined.notification.topic;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.quarantined.notification.common.AppConstants;
import com.quarantined.notification.exception.BadRequestException;
import com.quarantined.notification.exception.ResourceNotFoundException;
import com.quarantined.notification.security.UserPrincipal;
import com.quarantined.notification.subscribe.ChannelSubscriptionCount;
import com.quarantined.notification.subscribe.Subscription;
import com.quarantined.notification.subscribe.SubscriptionRepository;
import com.quarantined.notification.user.User;
import com.quarantined.notification.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TopicService {

	private final TopicRepository notificationTopicRepository;

	private final SubscriptionRepository subscriptionRepository;

	private final UserRepository userRepository;

	private static final Logger logger = LoggerFactory.getLogger(TopicService.class);

	public PagedResponse<TopicResponse> getAllTopics(UserPrincipal currentUser, int page, int size) {
		validatePageNumberAndSize(page, size);

		// Retrieve Topics
		final Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
		final Page<Topic> topics = notificationTopicRepository.findAll(pageable);

		if (topics.getNumberOfElements() == 0) {
			return new PagedResponse<>(Collections.emptyList(), topics.getNumber(), topics.getSize(),
					topics.getTotalElements(), topics.getTotalPages(), topics.isLast());
		}

		// Map Topics to TopicsResponses containing subscription counts and topic
		// creator details
		List<Long> topicIds = topics.map(Topic::getId).getContent();
		Map<Long, Long> channelSubscriptionCountMap = getChannelSubscriptionCountMap(topicIds);
		Map<Long, Long> topicUserVoteMap = getTopicUserSubscriptionMap(currentUser, topicIds);
		Map<Long, User> creatorMap = getTopicCreatorMap(topics.getContent());

		List<TopicResponse> topicResponses = topics.map(topic -> {
			return ModelMapper.mapTopicToTopicResponse(topic, channelSubscriptionCountMap, creatorMap.get(topic.getCreatedBy()),
					topicUserVoteMap == null ? null : topicUserVoteMap.getOrDefault(topic.getId(), null));
		}).getContent();

		return new PagedResponse<>(topicResponses, topics.getNumber(), topics.getSize(), topics.getTotalElements(),
				topics.getTotalPages(), topics.isLast());
	}

	public PagedResponse<TopicResponse> getTopicsCreatedBy(String username, UserPrincipal currentUser, int page,
			int size) {
		validatePageNumberAndSize(page, size);

		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

		// Retrieve all topics created by the given username
		Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
		Page<Topic> topics = notificationTopicRepository.findByCreatedBy(user.getId(), pageable);

		if (topics.getNumberOfElements() == 0) {
			return new PagedResponse<>(Collections.emptyList(), topics.getNumber(), topics.getSize(),
					topics.getTotalElements(), topics.getTotalPages(), topics.isLast());
		}

		// Map Topics to TopicResponses containing Subscription counts and Topic creator
		// details
		List<Long> topicIds = topics.map(Topic::getId).getContent();
		Map<Long, Long> channelSubscriptionCountMap = getChannelSubscriptionCountMap(topicIds);
		Map<Long, Long> topicUserVoteMap = getTopicUserSubscriptionMap(currentUser, topicIds);

		List<TopicResponse> topicResponses = topics.map(topic -> {
			return ModelMapper.mapTopicToTopicResponse(topic, channelSubscriptionCountMap, user,
					topicUserVoteMap == null ? null : topicUserVoteMap.getOrDefault(topic.getId(), null));
		}).getContent();

		return new PagedResponse<>(topicResponses, topics.getNumber(), topics.getSize(), topics.getTotalElements(),
				topics.getTotalPages(), topics.isLast());
	}

	public PagedResponse<TopicResponse> getTopicsSubscribedBy(String username, UserPrincipal currentUser, int page,
			int size) {
		validatePageNumberAndSize(page, size);

		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

		// Retrieve all topicIds in which the given username has subscribed
		Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
		Page<Long> userSubscribedTopicIds = subscriptionRepository.findSubscribedTopicIdsByUserId(user.getId(), pageable);

		if (userSubscribedTopicIds.getNumberOfElements() == 0) {
			return new PagedResponse<>(Collections.emptyList(), userSubscribedTopicIds.getNumber(),
					userSubscribedTopicIds.getSize(), userSubscribedTopicIds.getTotalElements(), userSubscribedTopicIds.getTotalPages(),
					userSubscribedTopicIds.isLast());
		}

		// Retrieve all topic details from the voted topicIds.
		List<Long> topicIds = userSubscribedTopicIds.getContent();

		Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
		List<Topic> topics = notificationTopicRepository.findByIdIn(topicIds, sort);

		// Map Topic to TopicResponses containing vote counts and topic creator details
		Map<Long, Long> channelsubscriptionCountMap = getChannelSubscriptionCountMap(topicIds);
		Map<Long, Long> topicUsersubscriptionMap = getTopicUserSubscriptionMap(currentUser, topicIds);
		Map<Long, User> creatorMap = getTopicCreatorMap(topics);

		List<TopicResponse> topicResponses = topics.stream().map(topic -> {
			return ModelMapper.mapTopicToTopicResponse(topic, channelsubscriptionCountMap, creatorMap.get(topic.getCreatedBy()),
					topicUsersubscriptionMap == null ? null : topicUsersubscriptionMap.getOrDefault(topic.getId(), null));
		}).collect(Collectors.toList());

		return new PagedResponse<>(topicResponses, userSubscribedTopicIds.getNumber(), userSubscribedTopicIds.getSize(),
				userSubscribedTopicIds.getTotalElements(), userSubscribedTopicIds.getTotalPages(), userSubscribedTopicIds.isLast());
	}

	public Topic createTopic(TopicRequest topicRequest) {
		Topic topic = new Topic();
		topic.setName(topicRequest.getName());

		topicRequest.getChannels().stream() /* */
				.map(channel -> new Channel(channel.getName())) /* */
				.forEach(topic::addChannel);

		Instant expirationDateTime = Instant.now().plus(Duration.ofDays(topicRequest.getTopicLength().getDays()))
				.plus(Duration.ofHours(topicRequest.getTopicLength().getHours()));

		topic.setExpirationDateTime(expirationDateTime);

		return notificationTopicRepository.save(topic);
	}

	public TopicResponse getTopicById(Long topicId, UserPrincipal currentUser) {
		Topic topic = notificationTopicRepository.findById(topicId)
				.orElseThrow(() -> new ResourceNotFoundException("Topic", "id", topicId));

		// Retrieve subscription Counts of every channel belonging to the current topic
		List<ChannelSubscriptionCount> votes = subscriptionRepository.countByTopicIdGroupByChannelId(topicId);

		Map<Long, Long> channelSubscriptionMap = votes.stream().collect(Collectors
				.toMap(ChannelSubscriptionCount::getChannelId, ChannelSubscriptionCount::getSubscriptionCount));

		// Retrieve topic creator details
		User creator = userRepository.findById(topic.getCreatedBy())
				.orElseThrow(() -> new ResourceNotFoundException("User", "id", topic.getCreatedBy()));

		// Retrieve subscription done by logged in user
		Subscription userSubscription = null;
		if (currentUser != null) {
			userSubscription = subscriptionRepository.findByUserIdAndTopicId(currentUser.getId(), topicId);
		}

		return ModelMapper.mapTopicToTopicResponse(topic, channelSubscriptionMap, creator,
				userSubscription != null ? userSubscription.getChannel().getId() : null);
	}

	private void validatePageNumberAndSize(int page, int size) {
		if (page < 0) {
			throw new BadRequestException("Page number cannot be less than zero.");
		}

		if (size > AppConstants.MAX_PAGE_SIZE) {
			throw new BadRequestException("Page size must not be greater than " + AppConstants.MAX_PAGE_SIZE);
		}
	}

	private Map<Long, Long> getChannelSubscriptionCountMap(List<Long> topicIds) {

		// Retrieve Subscription Counts of every Channel belonging to the given topicIds
		return subscriptionRepository.countByTopicIdInGroupByChannelId(topicIds).stream().collect(Collectors
				.toMap(ChannelSubscriptionCount::getChannelId, ChannelSubscriptionCount::getSubscriptionCount));
	}

	private Map<Long, Long> getTopicUserSubscriptionMap(UserPrincipal currentUser, List<Long> topicIds) {

		// Retrieve Subscriptions done by the logged in user to the given topicIds
		return ofNullable(currentUser) /* */
				.map(user -> subscriptionRepository.findByUserIdAndTopicIdIn(user.getId(), topicIds) /* */
						.stream() /* */
						.collect(Collectors.toMap(subscription -> subscription.getTopic().getId(),
								subscription -> subscription.getChannel().getId())))
				.orElse(new HashMap<Long, Long>());
	}

	private Map<Long, User> getTopicCreatorMap(List<Topic> topics) {

		// Get Topic Creator details of the given list of topic
		List<Long> creatorIds = topics.stream().map(Topic::getCreatedBy).distinct().collect(toList());

		List<User> creators = userRepository.findByIdIn(creatorIds);
		Map<Long, User> creatorMap = creators.stream().collect(Collectors.toMap(User::getId, Function.identity()));

		return creatorMap;
	}
}
