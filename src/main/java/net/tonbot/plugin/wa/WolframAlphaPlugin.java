package net.tonbot.plugin.wa;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

import net.tonbot.common.Activity;
import net.tonbot.common.TonbotPlugin;
import net.tonbot.common.TonbotPluginArgs;

public class WolframAlphaPlugin extends TonbotPlugin {

	private Injector injector;

	public WolframAlphaPlugin(TonbotPluginArgs args) {
		super(args);

		ObjectMapper objectMapper = new ObjectMapper();

		File configFile = args.getConfigFile();
		if (!configFile.exists()) {
			// TODO: Create it.
			throw new IllegalStateException("Config file doesn't exist.");
		}

		Preconditions.checkNotNull(configFile, "configFile must be non-null.");

		try {
			Config config = objectMapper.readValue(configFile, Config.class);
			this.injector = Guice.createInjector(
					new WolframAlphaModule(args.getPrefix(), args.getBotUtils(), config.getWolframAlphaAppId()));
		} catch (IOException e) {
			throw new RuntimeException("Could not read configuration file.", e);
		}
	}

	@Override
	public String getFriendlyName() {
		return "Wolfram Alpha";
	}

	@Override
	public String getActionDescription() {
		return "Ask Wolfram Alpha";
	}

	@Override
	public boolean isHidden() {
		return false;
	}

	@Override
	public Set<Activity> getActivities() {
		return injector.getInstance(Key.get(new TypeLiteral<Set<Activity>>() {
		}));
	}
}
