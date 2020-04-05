package com.quarantined.notification.topic;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.quarantined.notification.common.UserDateAudit;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "topic")
public class Topic extends UserDateAudit {

	private static final long serialVersionUID = -1527553099517615600L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	@Size(max = 140)
	private String name;

	@OneToMany(mappedBy = "topic", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
	@Size(min = 2, max = 6)
	@Fetch(FetchMode.SELECT)
	@BatchSize(size = 30)
	private List<Channel> channels = new ArrayList<>();

	@NotNull
	private Instant expirationDateTime;

	public void addChannel(Channel channel) {
		channels.add(channel);
		channel.setTopic(this);
	}

	public void removeChannel(Channel channel) {
		channels.remove(channel);
		channel.setTopic(null);
	}
}
