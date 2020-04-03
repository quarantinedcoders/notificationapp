package com.notification.api.covid19;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IndiaOrgData {

	@JsonProperty("cases_time_series")
	private List<CasesTimeSeries> casesTimeSeriesList;

	@JsonProperty("key_values")
	private List<KeyValues> keyValuesList;

	@JsonProperty("statewise")
	private List<StateWise> stateWiseList;

	@JsonProperty("tested")
	private List<Tested> testedList;

	@Getter
	@Setter
	static class CasesTimeSeries {
		private String dailyconfirmed;
		private String dailydeceased;
		private String dailyrecovered;
		private String date;
		private String death;
		private String rec;
		private String totalconfirmed;
		private String totaldeceased;
		private String totalrecovered;
	}

	@Getter
	@Setter
	static class KeyValues {
		private String confirmeddelta;
		private String counterforautotimeupdate;
		private String deceaseddelta;
		private String lastupdatedtime;
		private String recovereddelta;
		private String statesdelta;
	}

	@Getter
	@Setter
	static class StateWise {
		private String active;
		private String confirmed;
		private String deaths;
		private Delta delta;
		private String lastupdatedtime;
		private String recovered;
		private String state;
		private String statecode;
	}

	@Getter
	@Setter
	static class Delta {
		private int active;
		private int confirmed;
		private int deaths;
		private int recovered;
	}

	@Getter
	@Setter
	static class Tested {
		private String source;
		private String testsconductedbyprivatelabs;
		private String totalindividualstested;
		private String totalpositivecases;
		private String totalsamplestested;
		private String updatetimestamp;
	}
}
