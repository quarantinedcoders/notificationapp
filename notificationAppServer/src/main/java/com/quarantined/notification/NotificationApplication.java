package com.quarantined.notification;

import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@EntityScan(basePackageClasses = { NotificationApplication.class, Jsr310JpaConverters.class })
public class NotificationApplication {

	private static final String UTC = "UTC";

	@PostConstruct
	void init() {
		TimeZone.setDefault(TimeZone.getTimeZone(UTC));
	}

	public static void main(String[] args) {
		SpringApplication.run(NotificationApplication.class, args);
	}
}
