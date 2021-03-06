package com.quarantined.notification.covid19;

import static java.lang.Integer.parseInt;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quarantined.notification.covid19.IndiaOrgData.StateWise;
import com.quarantined.notification.http.SiteReaderI;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class IndiaOrgService implements Covid19ServiceI {

	private static final String COUNTRY_INDIA = "INDIA";

	private static final String COVID19_INDIA_ORG_DATA_JSON = "https://api.covid19india.org/data.json";

	private final SiteReaderI siteReader;

	@Override
	public List<Covid19Data> getStateWiseSummary() {

		var read = siteReader.read(COVID19_INDIA_ORG_DATA_JSON);

		ObjectMapper objectMapper = new ObjectMapper();
		try {
			IndiaOrgData covid19Data = objectMapper.readValue(read, IndiaOrgData.class);
			return covid19Data.getStateWiseList().stream().map(this::transformCovid19Data).collect(Collectors.toList());
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}

	private Covid19Data transformCovid19Data(StateWise stateWise) {

		return Covid19Data.builder().active(parseInt(stateWise.getActive()))
				.confirmed(parseInt(stateWise.getConfirmed())).deaths(parseInt(stateWise.getDeaths()))
				.lastupdatedtime(stateWise.getLastupdatedtime()).recovered(parseInt(stateWise.getRecovered()))
				.state(stateWise.getState()).country(COUNTRY_INDIA).build();
	}
}
