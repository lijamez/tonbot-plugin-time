package net.tonbot.plugin.wa;

import java.util.Set;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import net.tonbot.common.Activity;
import net.tonbot.common.BotUtils;
import net.tonbot.common.Prefix;
import sx.blah.discord.api.IDiscordClient;

class WolframAlphaModule extends AbstractModule {

	private final IDiscordClient discordClient;
	private final String prefix;
	private final BotUtils botUtils;
	private final String wolframAlphaAppId;

	public WolframAlphaModule(
			IDiscordClient discordClient,
			String prefix,
			BotUtils botUtils,
			String wolframAlphaAppId) {
		this.discordClient = Preconditions.checkNotNull(discordClient, "discordClient must be non-null.");
		this.prefix = Preconditions.checkNotNull(prefix, "prefix must be non-null.");
		this.botUtils = Preconditions.checkNotNull(botUtils, "botUtils must be non-null.");
		this.wolframAlphaAppId = Preconditions.checkNotNull(wolframAlphaAppId, "wolframAlphaAppId must be non-null.");
	}

	public void configure() {
		bind(IDiscordClient.class).toInstance(discordClient);
		bind(String.class).annotatedWith(Prefix.class).toInstance(prefix);
		bind(BotUtils.class).toInstance(botUtils);
		bind(String.class).annotatedWith(WolframAlphaAppId.class).toInstance(wolframAlphaAppId);
	}

	@Provides
	@Singleton
	HttpClient httpClient() {
		return HttpClients.createDefault();
	}

	@Provides
	@Singleton
	Set<Activity> activities(WolframActivity wolframActivity) {
		return ImmutableSet.of(wolframActivity);
	}
}
