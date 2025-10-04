package dev.goldmensch.jdacbot;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import dev.goldmensch.jdacbot.data.ConfigRepository;
import dev.goldmensch.jdacbot.webhook.GhWebhook;

public class JDACBotModule extends AbstractModule {

    private final ConfigRepository configRepository;
    private final GhWebhook ghWebhook;

    public JDACBotModule(ConfigRepository configRepository, GhWebhook ghWebhook) {
        this.configRepository = configRepository;
        this.ghWebhook = ghWebhook;
    }


    @Provides
    public GhWebhook ghWebhook() {
        return ghWebhook;
    }

    @Provides
    public ConfigRepository configRepository() {
        return configRepository;
    }
}
