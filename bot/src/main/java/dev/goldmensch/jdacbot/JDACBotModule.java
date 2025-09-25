package dev.goldmensch.jdacbot;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import dev.goldmensch.jdacbot.webhook.GhWebhook;

public class JDACBotModule extends AbstractModule {

    private final GhWebhook ghWebhook;

    public JDACBotModule(GhWebhook ghWebhook) {
        this.ghWebhook = ghWebhook;
    }


    @Provides
    public GhWebhook ghWebhook() {
        return ghWebhook;
    }
}
