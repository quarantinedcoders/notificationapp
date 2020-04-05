package com.quarantined.notification.jobs;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.quarantined.notification.broadcast.BroadCastServiceI;
import com.quarantined.notification.covid19.Covid19Data;
import com.quarantined.notification.covid19.Covid19ServiceI;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class NotificationJobs {

	private static final Map<String, Integer> STATE_WISE_CORONA_CASES = new HashMap<>();

	private final Covid19ServiceI covid19Service;
	private final BroadCastServiceI broadCastServiceI;

	@Scheduled(fixedDelay = 30000)
	public void checkCovid19CountInIndia() {

		System.out.println("Job started - checkCovid19CountInIndia");

		final var stateList = covid19Service.getStateWiseSummary();
		System.out.println("Covid19 cases increased in " + stateList.size() + "states");

		StringBuilder message = new StringBuilder();
		for (Covid19Data state : stateList) {
			int lastConfirmedCount = STATE_WISE_CORONA_CASES.getOrDefault(state.getState(), 0);
			int diff = state.getConfirmed() - lastConfirmedCount;
			if (diff > 0) {
				message.append(state.getState() + " " + state.getConfirmed() + "^" + diff + "\n");
				STATE_WISE_CORONA_CASES.put(state.getState(), state.getConfirmed());
			}
		}

		if (message.length() > 0) {
			System.out.println("There is rise in Covid19 cases, sending notification ");
			broadCastServiceI.send(message.toString());
		} else {
			System.out.println("No change in Covid19 count for India");
		}
		System.out.println("Job ended - checkCovid19CountInIndia");
	}
}
