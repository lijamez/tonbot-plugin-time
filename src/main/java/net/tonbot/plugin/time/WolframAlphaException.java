package net.tonbot.plugin.time;

@SuppressWarnings("serial")
class WolframAlphaException extends RuntimeException {

	public WolframAlphaException(String message, Exception causedBy) {
		super(message, causedBy);
	}

	public WolframAlphaException(String message) {
		super(message);
	}
}
