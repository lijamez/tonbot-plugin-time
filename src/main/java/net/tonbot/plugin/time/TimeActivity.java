package net.tonbot.plugin.time;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;

import net.tonbot.common.Activity;
import net.tonbot.common.ActivityDescriptor;
import net.tonbot.common.BotUtils;
import net.tonbot.common.TonbotBusinessException;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.EmbedBuilder;

public class TimeActivity implements Activity {

	private static final ActivityDescriptor ACTIVITY_DESCRIPTOR = ActivityDescriptor.builder()
			.route("time")
			.parameters(ImmutableList.of("query"))
			.description("Anything about time. Conversions, current time, etc.")
			.build();

	private final BotUtils botUtils;
	private final WolframAlphaClient waClient;

	@Inject
	public TimeActivity(BotUtils botUtils, WolframAlphaClient waClient) {
		this.botUtils = Preconditions.checkNotNull(botUtils, "botUtils must be non-null.");
		this.waClient = Preconditions.checkNotNull(waClient, "waClient must be non-null.");
	}

	@Override
	public ActivityDescriptor getDescriptor() {
		return ACTIVITY_DESCRIPTOR;
	}

	@Override
	public void enact(MessageReceivedEvent event, String args) {

		WolframAlphaQueryResponse response = waClient.query(args);

		QueryResult queryResult = response.getQueryResult();

		if (!queryResult.isSuccess()) {
			throw new TonbotBusinessException("I don't know what that means. :worried:");
		}

		Pod resultPod = queryResult.getPods().stream()
				.filter(pod -> StringUtils.equals(pod.getId(), "Result"))
				.findAny()
				.orElse(null);

		Pod clockImagesPod = queryResult.getPods().stream()
				.filter(pod -> StringUtils.equals(pod.getId(), "ClockImages")
						|| StringUtils.equals(pod.getId(), "ClockImage"))
				.findAny()
				.orElse(null);

		// Use the presence of a clock image to determine if query was time-related.
		if (clockImagesPod == null) {
			throw new TonbotBusinessException("I don't think that was related to time. :thinking:");
		}

		EmbedBuilder embedBuilder = new EmbedBuilder();

		if (!clockImagesPod.getSubpods().isEmpty()) {
			embedBuilder.withImage(clockImagesPod.getSubpods().get(0).getImage().getSrc());
		}

		if (resultPod != null) {
			embedBuilder.withDescription(resultPod.getSubpods().get(0).getPlaintext());
		}

		embedBuilder.withFooterText("Powered by WolframAlpha");

		botUtils.sendEmbed(event.getChannel(), embedBuilder.build());
	}
}
