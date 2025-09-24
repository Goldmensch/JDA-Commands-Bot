package dev.goldmensch.jdacbot.cmd;

import com.github.kaktushose.jda.commands.annotations.interactions.Command;
import com.github.kaktushose.jda.commands.annotations.interactions.Interaction;
import com.github.kaktushose.jda.commands.annotations.interactions.Param;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.CommandEvent;
import dev.goldmensch.jdacbot.service.webhook.GHWebhook;
import jakarta.inject.Inject;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

@Interaction
public class ReleaseChannel {

    private final GHWebhook hook;

    @Inject
    public ReleaseChannel(GHWebhook hook) {
        this.hook = hook;
    }


    @Command("release")
    public void onCommand(CommandEvent event, @Param("set") boolean release) {
        if (release) {
            hook.registerChannel(((TextChannel) event.getChannel()));
            event.reply("Channel registered for releases!");
        } else {
            hook.unregisterChannel(((TextChannel) event.getChannel()));
            event.reply("Channel unregistered for releases!");
        }
    }
}
