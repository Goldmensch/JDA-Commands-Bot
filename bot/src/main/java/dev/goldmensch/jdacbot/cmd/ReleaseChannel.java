package dev.goldmensch.jdacbot.cmd;

import com.github.kaktushose.jda.commands.annotations.interactions.Command;
import com.github.kaktushose.jda.commands.annotations.interactions.Interaction;
import com.github.kaktushose.jda.commands.annotations.interactions.Param;
import com.github.kaktushose.jda.commands.annotations.interactions.Permissions;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.CommandEvent;
import dev.goldmensch.jdacbot.webhook.GhWebhook;
import jakarta.inject.Inject;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

@Interaction
public class ReleaseChannel {

    private final GhWebhook hook;

    @Inject
    public ReleaseChannel(GhWebhook hook) {
        this.hook = hook;
    }


    @Permissions("ADMINISTRATOR")
    @Command("release")
    public void onCommand(CommandEvent event, @Param("set") boolean release) {
        if (event.getChannel() instanceof TextChannel textChannel) {
            if (release) {
                hook.registerChannel(textChannel);
                event.reply("Channel registered for releases!");
            } else {
                hook.unregisterChannel(textChannel);
                event.reply("Channel unregistered for releases!");
            }
            return;
        }

        event.reply("Channel must be a text channel!");
    }
}
