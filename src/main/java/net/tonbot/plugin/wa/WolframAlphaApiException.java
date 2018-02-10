package net.tonbot.plugin.wa;

import lombok.Getter;

@SuppressWarnings("serial")
class WolframAlphaApiException extends RuntimeException {

	@Getter
	private final int statusCode;

	public WolframAlphaApiException(String message, int statusCode, Exception causedBy) {
		super(message, causedBy);
		this.statusCode = statusCode;
	}

	public WolframAlphaApiException(String message, int statusCode) {
		super(message);
		this.statusCode = statusCode;
	}
}
