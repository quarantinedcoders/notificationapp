package com.quarantined.notification.covid19;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class Covid19Data {

	private int active;
	private int confirmed;
	private int deaths;
	private Delta delta;
	private String lastupdatedtime;
	private int recovered;
	private String state;
	private String country;

	@Getter
	@Setter
	@Builder
	@AllArgsConstructor
	public static class Delta {
		private int active;
		private int confirmed;
		private int deaths;
		private int recovered;
	}
}
