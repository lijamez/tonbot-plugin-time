package net.tonbot.plugin.wa;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;

import net.tonbot.common.Activity;
import net.tonbot.common.ActivityDescriptor;
import net.tonbot.common.ActivityUsageException;
import net.tonbot.common.BotUtils;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

public class WolframActivity implements Activity {

	private static final ActivityDescriptor ACTIVITY_DESCRIPTOR = ActivityDescriptor.builder()
			.route("w")
			.parameters(ImmutableList.of("<a question>"))
			.description("Ask WolframAlpha about anything.")
			.build();

	private final BotUtils botUtils;
	private final WolframAlphaClient waClient;

	@Inject
	public WolframActivity(BotUtils botUtils, WolframAlphaClient waClient) {
		this.botUtils = Preconditions.checkNotNull(botUtils, "botUtils must be non-null.");
		this.waClient = Preconditions.checkNotNull(waClient, "waClient must be non-null.");
	}

	@Override
	public ActivityDescriptor getDescriptor() {
		return ACTIVITY_DESCRIPTOR;
	}

	@Override
	public void enact(MessageReceivedEvent event, String args) {

		if (StringUtils.isBlank(args)) {
			throw new ActivityUsageException("What do you want to ask?");
		}

		event.getChannel().setTypingStatus(true);

		String response;

		try {
			response = waClient.result(args);
		} catch (WolframAlphaApiException e) {
			if (e.getStatusCode() == 501) {
				// Answer not known.
				response = "Sorry, I don't know.";
			} else {
				event.getChannel().setTypingStatus(false);
				throw e;
			}
		}

		botUtils.sendMessage(event.getChannel(), response);
	}
}
