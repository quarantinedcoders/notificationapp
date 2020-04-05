package com.quarantined.notification.topic;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import com.quarantined.notification.user.User;
import com.quarantined.notification.user.UserSummary;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ModelMapper {

	public static TopicResponse mapTopicToTopicResponse(Topic topic, Map<Long, Long> channelSubscriptionsMap,
			User creator, Long userSubscription) {

		TopicResponse topicResponse = TopicResponse.builder().id(topic.getId()).name(topic.getName())
				.creationDateTime(topic.getCreatedAt()).expirationDateTime(topic.getExpirationDateTime())
				.isExpired(topic.getExpirationDateTime().isBefore(Instant.now())).build();

		List<ChannelResponse> channelResponses = topic.getChannels().stream().map(channel -> {
			ChannelResponse channelResponse = new ChannelResponse();
			channelResponse.setId(channel.getId());
			channelResponse.setName(channel.getName());

			ofNullable(channelSubscriptionsMap.get(channel.getId())).ifPresent(channelResponse::setSubscriptionCount);

			return channelResponse;
		}).collect(toList());

		topicResponse.setChannels(channelResponses);
		UserSummary creatorSummary = new UserSummary(creator.getId(), creator.getUsername(), creator.getName());
		topicResponse.setCreatedBy(creatorSummary);

		if (userSubscription != null) {
			topicResponse.setSelectedChannel(userSubscription);
		}

		long totalSubscription = topicResponse.getChannels().stream().mapToLong(ChannelResponse::getSubscriptionCount)
				.sum();
		topicResponse.setTotalSubscriptions(totalSubscription);

		return topicResponse;
	}
}
