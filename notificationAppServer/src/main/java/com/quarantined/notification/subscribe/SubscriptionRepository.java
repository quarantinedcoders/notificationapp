package com.quarantined.notification.subscribe;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
	@Query("SELECT NEW com.quarantined.notification.subscribe.ChannelSubscriptionCount(s.channel.id, count(s.id)) FROM Subscription s WHERE s.topic.id in :topicIds GROUP BY s.channel.id")
	List<ChannelSubscriptionCount> countByTopicIdInGroupByChannelId(@Param("topicIds") List<Long> topicIds);

	@Query("SELECT NEW com.quarantined.notification.subscribe.ChannelSubscriptionCount(s.channel.id, count(s.id)) FROM Subscription s WHERE s.topic.id = :topicId GROUP BY s.channel.id")
	List<ChannelSubscriptionCount> countByTopicIdGroupByChannelId(@Param("topicId") Long topicId);

	@Query("SELECT s FROM Subscription s where s.user.id = :userId and s.topic.id in :topicIds")
	List<Subscription> findByUserIdAndTopicIdIn(@Param("userId") Long userId, @Param("topicIds") List<Long> topicIds);

	@Query("SELECT s FROM Subscription s where s.user.id = :userId and s.topic.id = :topicId")
	Subscription findByUserIdAndTopicId(@Param("userId") Long userId, @Param("topicId") Long topicId);

	@Query("SELECT COUNT(s.id) from Subscription s where s.user.id = :userId")
	long countByUserId(@Param("userId") Long userId);

	@Query("SELECT s.topic.id FROM Subscription s WHERE s.user.id = :userId")
	Page<Long> findSubscribedTopicIdsByUserId(@Param("userId") Long userId, Pageable pageable);
}
