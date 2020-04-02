package com.notification.api.http;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.springframework.stereotype.Component;

@Component
public class HttpClientSiteReader implements SiteReaderI {

	@Override
	public String read(String url) {

		var httpClient = HttpClient.newHttpClient();

		var httpRequest = HttpRequest.newBuilder() /* */
				.uri(URI.create(url)) /* */
				.GET() /* */
				.build();

		try {
			HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
			return httpResponse.body();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			return e.getMessage();
		}
	}
}
