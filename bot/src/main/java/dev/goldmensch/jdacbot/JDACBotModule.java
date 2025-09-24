package dev.goldmensch.jdacbot;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import dev.goldmensch.jdacbot.service.webhook.GHWebhook;

public class JDACBotModule extends AbstractModule {

    private final GHWebhook ghWebhook;

    public JDACBotModule(GHWebhook ghWebhook) {
        this.ghWebhook = ghWebhook;
    }


    @Provides
    public GHWebhook ghWebhook() {
        return ghWebhook;
    }
}
