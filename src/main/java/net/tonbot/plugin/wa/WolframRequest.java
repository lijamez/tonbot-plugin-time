package net.tonbot.plugin.wa;

import javax.annotation.Nonnull;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import net.tonbot.common.Param;

@ToString()
@EqualsAndHashCode
class WolframRequest {

	@Getter
	@Param(name = "query", ordinal = 0, description = "A query", captureRemaining = true)
	@Nonnull
	private String query;
}
