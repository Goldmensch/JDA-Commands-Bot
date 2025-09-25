package dev.goldmensch.jdacbot;

import com.github.kaktushose.jda.commands.JDACommands;
import com.github.kaktushose.jda.commands.guice.GuiceExtensionData;
import com.google.inject.Guice;
import com.google.inject.Injector;
import dev.goldmensch.jdacbot.webhook.GhWebhook;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        JDA jda = JDABuilder.createDefault(System.getenv("BOT_TOKEN"))
                .build();

        GhWebhook hook;
        try {
             hook = GhWebhook.start();
        } catch (Exception e) {
            jda.shutdown();
            throw e;
        }

        Injector injector = Guice.createInjector(new JDACBotModule(hook));

        JDACommands.builder(jda, Main.class)
                .extensionData(new GuiceExtensionData(injector))
                .start();
    }
}
