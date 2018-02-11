package net.tonbot.plugin.wa;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.inject.Inject;

class WolframAlphaClient {

	private final String appId;
	private final CloseableHttpClient httpClient;

	@Inject
	public WolframAlphaClient(@WolframAlphaAppId String appId, CloseableHttpClient httpClient) {
		this.appId = Preconditions.checkNotNull(appId, "appId must be non-null.");
		this.httpClient = Preconditions.checkNotNull(httpClient, "httpClient must be non-null.");
	}

	/**
	 * Calls Wolfram Alpha's Short Answers API.
	 * https://products.wolframalpha.com/short-answers-api/documentation/
	 * 
	 * @param input
	 *            The input. Non-null
	 * @return Wolfram Alpha's answer.
	 * @throw WolframAlphaApiException if a non-200 status code is returned. A
	 *        status code of 501 indicates that Wolfram Alpha doesn't know how to
	 *        handle the input.
	 */
	public String getShortAnswer(String input) {
		Preconditions.checkNotNull(input, "input must be non-null.");

		try {
			URI uri = new URIBuilder()
					.setScheme("https")
					.setHost("api.wolframalpha.com")
					.setPath("/v1/result")
					.setParameter("appid", appId)
					.setParameter("i", input)
					.build();

			return get(uri);
		} catch (URISyntaxException e) {
			throw new IllegalStateException("Unable to make Wolfram Alpha call.", e);
		}
	}

	/**
	 * Calls Wolfram Alpha's Spoken Results API.
	 * https://products.wolframalpha.com/spoken-results-api/documentation/
	 * 
	 * @param input
	 *            The input. Non-null
	 * @return Wolfram Alpha's answer.
	 * @throw WolframAlphaApiException if a non-200 status code is returned. A
	 *        status code of 501 indicates that Wolfram Alpha doesn't know how to
	 *        handle the input.
	 */
	public String getSpokenAnswer(String input) {
		Preconditions.checkNotNull(input, "input must be non-null.");

		try {
			URI uri = new URIBuilder()
					.setScheme("https")
					.setHost("api.wolframalpha.com")
					.setPath("/v1/spoken")
					.setParameter("appid", appId)
					.setParameter("i", input)
					.build();

			return get(uri);
		} catch (URISyntaxException e) {
			throw new IllegalStateException("Unable to make Wolfram Alpha call.", e);
		}
	}

	private String get(URI uri) {

		try {
			HttpGet httpGet = new HttpGet(uri);

			CloseableHttpResponse response = httpClient.execute(httpGet);

			try {
				int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode != 200) {
					throw new WolframAlphaApiException(
							"Wolfram Alpha returned a " + response.getStatusLine().getStatusCode() + " status code.",
							statusCode);
				}

				StringWriter writer = new StringWriter();
				IOUtils.copy(response.getEntity().getContent(), writer, Charsets.UTF_8);

				EntityUtils.consumeQuietly(response.getEntity());

				return writer.toString();
			} finally {
				response.close();
			}
		} catch (IOException e) {
			throw new UncheckedIOException("Failed to make Wolfram Alpha call.", e);
		}
	}
}
