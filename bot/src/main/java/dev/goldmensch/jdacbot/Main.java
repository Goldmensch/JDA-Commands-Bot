package dev.goldmensch.jdacbot;

import com.github.kaktushose.jda.commands.JDACommands;
import com.github.kaktushose.jda.commands.guice.GuiceExtensionData;
import com.google.inject.Guice;
import com.google.inject.Injector;
import dev.goldmensch.jdacbot.service.webhook.GHWebhook;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        JDA jda = JDABuilder.createDefault(System.getenv("BOT_TOKEN"))
                .build();

        GHWebhook hook = GHWebhook.start();

        Injector injector = Guice.createInjector(new JDACBotModule(hook));

        JDACommands.builder(jda, Main.class)
                .extensionData(new GuiceExtensionData(injector))
                .start();
    }
}
