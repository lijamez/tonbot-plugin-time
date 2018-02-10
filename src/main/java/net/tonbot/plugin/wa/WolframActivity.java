package net.tonbot.plugin.wa;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.vdurmont.emoji.EmojiParser;

import net.tonbot.common.Activity;
import net.tonbot.common.ActivityDescriptor;
import net.tonbot.common.ActivityUsageException;
import net.tonbot.common.BotUtils;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.MessageTokenizer;

public class WolframActivity implements Activity {

	private static final ActivityDescriptor ACTIVITY_DESCRIPTOR = ActivityDescriptor.builder()
			.route("")
			.parameters(ImmutableList.of("<question>"))
			.build();

	private final IDiscordClient discordClient;
	private final BotUtils botUtils;
	private final WolframAlphaClient waClient;

	@Inject
	public WolframActivity(IDiscordClient discordClient, BotUtils botUtils, WolframAlphaClient waClient) {
		this.discordClient = Preconditions.checkNotNull(discordClient, "discordClient must be non-null.");
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

		String normalizedQuery = normalize(args);

		event.getChannel().setTypingStatus(true);

		String response;

		try {
			response = waClient.result(normalizedQuery);
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

	private String normalize(String message) {

		StringBuilder normalizedMessage = new StringBuilder();

		// Remove all UTF-8 emojis
		String emojilessMessage = EmojiParser.removeAllEmojis(message);

		MessageTokenizer tokenizer = new MessageTokenizer(discordClient, emojilessMessage);

		while (tokenizer.hasNextWord()) {
			String nextWord = tokenizer.nextWord().getContent();

			if (MessageTokenizer.CUSTOM_EMOJI_PATTERN.matcher(nextWord).matches()) {
				// Custom emoji found. Ignore it.
				continue;
			}

			normalizedMessage.append(nextWord).append(" ");
		}

		return normalizedMessage.toString();
	}
}
