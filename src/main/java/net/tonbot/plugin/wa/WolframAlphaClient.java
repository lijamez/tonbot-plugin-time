package net.tonbot.plugin.wa;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.inject.Inject;

class WolframAlphaClient {

	private final String appId;
	private final HttpClient httpClient;

	@Inject
	public WolframAlphaClient(@WolframAlphaAppId String appId, HttpClient httpClient) {
		this.appId = Preconditions.checkNotNull(appId, "appId must be non-null.");
		this.httpClient = Preconditions.checkNotNull(httpClient, "httpClient must be non-null.");
	}

	public String result(String input) {
		Preconditions.checkNotNull(input, "input must be non-null.");

		try {
			URI uri = new URIBuilder()
					.setScheme("https") 
					.setHost("api.wolframalpha.com")
					.setPath("/v1/result")
					.setParameter("appid", appId)
					.setParameter("i", input)
					.build();

			HttpGet httpGet = new HttpGet(uri);

			HttpResponse response = httpClient.execute(httpGet);

			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != 200) {
				throw new WolframAlphaApiException(
						"Wolfram Alpha returned a " + response.getStatusLine().getStatusCode() + " status code.",
						statusCode);
			}

			StringWriter writer = new StringWriter();
			IOUtils.copy(response.getEntity().getContent(), writer, Charsets.UTF_8);
			return writer.toString();

		} catch (URISyntaxException e) {
			throw new IllegalStateException("Unable to make Wolfram Alpha call.", e);
		} catch (IOException e) {
			throw new UncheckedIOException("Failed to make Wolfram Alpha call.", e);
		}
	}
}
