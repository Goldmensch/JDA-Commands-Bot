package dev.goldmensch.jdacbot.cmd;

import com.github.kaktushose.jda.commands.annotations.interactions.Command;
import com.github.kaktushose.jda.commands.annotations.interactions.Interaction;
import com.github.kaktushose.jda.commands.annotations.interactions.Param;
import com.github.kaktushose.jda.commands.annotations.interactions.Permissions;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.CommandEvent;
import dev.goldmensch.jdacbot.data.ConfigRepository;
import jakarta.inject.Inject;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.stream.Collectors;

@Interaction
public class ReleaseChannel {

    private final ConfigRepository config;

    @Inject
    public ReleaseChannel(ConfigRepository config) {
        this.config = config;
    }

    @Permissions("ADMINISTRATOR")
    @Command("release set")
    public void onCommand(CommandEvent event, @Param("active") boolean release) {
        if (event.getChannel() instanceof TextChannel textChannel) {
            if (release) {
                config.addReleaseChannel(textChannel);
                event.reply("Channel registered for releases!");
            } else {
                config.removeReleaseChannel(textChannel);
                event.reply("Channel unregistered for releases!");
            }
            return;
        }

        event.reply("Channel must be a text channel!");
    }

    @Permissions("ADMINISTRATOR")
    @Command("release info")
    public void onCommand(CommandEvent event) {
        String channels = config.retrieveReleaseChannels()
                .stream()
                .map(Channel::getAsMention)
                .collect(Collectors.joining("\n"));

        event.reply("Currently following channels are registered as release channels: \n" + channels);
    }
}
