package net.tonbot.plugin.wa;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import net.tonbot.common.Param;

@ToString()
@EqualsAndHashCode
public class WolframRequest {

	@Getter
	@Param(name = "query", ordinal = 0, description = "A query", captureRemaining = true)
	private String query;
}
