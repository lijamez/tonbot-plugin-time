package net.tonbot.plugin.time;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
class Image {

	private final String src;

	@JsonCreator
	public Image(
			@JsonProperty("src") String src) {

		this.src = src;
	}

}
