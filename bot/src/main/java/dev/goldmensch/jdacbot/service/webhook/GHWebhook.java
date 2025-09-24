package dev.goldmensch.jdacbot.service.webhook;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import dev.goldmensch.jdacbot.service.webhook.pojo.Release;
import dev.goldmensch.jdacbot.service.webhook.pojo.ReleasePayload;
import jakarta.inject.Singleton;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class GHWebhook {
    private final Set<TextChannel> channels = ConcurrentHashMap.newKeySet();

    private static final Logger log = LoggerFactory.getLogger(GHWebhook.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    public static GHWebhook start() throws IOException {
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(8080), -1);

        GHWebhook ghWebhook = new GHWebhook();

        httpServer.createContext("/github", ghWebhook::handle);
        httpServer.start();

        return ghWebhook;
    }

    private void handle(HttpExchange exchange) throws IOException {
        String event = exchange.getRequestHeaders().getFirst("X-GitHub-Event");

        switch (event) {
            case "ping" -> log.debug("Received 'ping' on webhook");
            case "release" -> handleRelease(exchange);
            default -> log.warn("Received unknown event on webhook: {}", event);
        }

        sendOk(exchange);
    }

    private void handleRelease(HttpExchange exchange) throws IOException {
        ReleasePayload payload = objectMapper.readValue(exchange.getRequestBody(), ReleasePayload.class);
        if (!payload.action().equals("published")) return;
        System.out.println(payload);

        for (TextChannel channel : channels) {
            Release release = payload.release();
            String msg = """
                    # New Release: %s
                    
                    %s
                    
                    Visit release page [here](%s)
                    """.formatted(release.name(), release.body(), release.htmlUrl());

            for (int i = 0; i < msg.length(); i += 2000) {
                String submsg = msg.substring(i, Math.min(i + 2000, msg.length()));
                MessageCreateData data = new MessageCreateBuilder()
                        .setContent(submsg)
                        .setSuppressEmbeds(true)
                        .build();

                channel.sendMessage(data).queue();
            }
        }
    }

    private void sendOk(HttpExchange exchange) throws IOException {
        byte[] bytes = "Ok".getBytes();
        exchange.sendResponseHeaders(200, 0);

        try(OutputStream stream = exchange.getResponseBody()) {
            stream.write(bytes);
        }
    }

    public void registerChannel(TextChannel channel) {
        channels.add(channel);
    }

    public void unregisterChannel(TextChannel channel) {
        channels.remove(channel);
    }
}
