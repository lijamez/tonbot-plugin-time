package net.tonbot.plugin.wa;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;

import net.tonbot.common.Activity;
import net.tonbot.common.ActivityDescriptor;
import net.tonbot.common.ActivityUsageException;
import net.tonbot.common.BotUtils;
import net.tonbot.common.Enactable;
import net.tonbot.common.MessageNormalizer;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

public class WolframActivity implements Activity {

	private static final ActivityDescriptor ACTIVITY_DESCRIPTOR = ActivityDescriptor.builder()
			.route("")
			.parameters(ImmutableList.of("<question>"))
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

	@Enactable
	public void enact(MessageReceivedEvent event, WolframRequest request) {

		if (StringUtils.isBlank(request.getQuery())) {
			throw new ActivityUsageException("What do you want to ask?");
		}

		String normalizedQuery = MessageNormalizer.removeEmojis(request.getQuery());

		event.getChannel().setTypingStatus(true);

		String response;

		try {
			response = waClient.getSpokenAnswer(normalizedQuery);
		} catch (WolframAlphaApiException e) {
			if (e.getStatusCode() == 501) {
				// Answer not known.
				response = "Sorry, I don't know. :shrug:";
			} else {
				event.getChannel().setTypingStatus(false);
				throw e;
			}
		}

		botUtils.sendMessage(event.getChannel(), response);
	}
}
