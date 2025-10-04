package dev.goldmensch.jdacbot.webhook;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import dev.goldmensch.jdacbot.data.ConfigRepository;
import dev.goldmensch.jdacbot.webhook.pojo.ReleasePayload;
import jakarta.inject.Singleton;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.security.MessageDigest;

@Singleton
public class GhWebhook {
    private final HmacUtils hmacUtil;

    private static final Logger log = LoggerFactory.getLogger(GhWebhook.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ConfigRepository config;

    public GhWebhook(HmacUtils hmacUtil, ConfigRepository config) {
        this.hmacUtil = hmacUtil;
        this.config = config;
    }

    public static GhWebhook start(ConfigRepository config) throws IOException {
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(getPort()), 0);
        GhWebhook ghWebhook = new GhWebhook(createHmacUtil(), config);

        httpServer.createContext("/github", ghWebhook::handle);
        httpServer.start();
        log.info("Github Webhook started. Register events to: https://your_domain/github");

        return ghWebhook;
    }

    private static int getPort() {
        String raw = System.getenv("WEBHOOK_PORT");
        if (raw == null) return 8080;
        return Integer.parseInt(raw);
    }

    private static HmacUtils createHmacUtil() {
        String secret = System.getenv("WEBHOOK_SECRET");
        if (secret == null) {
            throw new RuntimeException("You have to set up an webhook secret using the 'WEBHOOK_SECRET' environment variable.");
        }

        return new HmacUtils(HmacAlgorithms.HMAC_SHA_256, secret);
    }

    private void handle(HttpExchange exchange) throws IOException {
        String event = exchange.getRequestHeaders().getFirst("X-GitHub-Event");
        String httpBody = new String(exchange.getRequestBody().readAllBytes());

        if (!validateExchange(exchange, httpBody)) {
            log.warn("Received unvalidated http exchange on github webhook from '{}', event: {}", exchange.getRemoteAddress(), event);
            return;
        }

        sendOk(exchange);

        switch (event) {
            case "ping" -> log.debug("Received 'ping' on webhook");
            case "release" -> handleRelease(httpBody);
            default -> log.warn("Received unknown event on webhook: {}", event);
        }

    }

    private boolean validateExchange(HttpExchange exchange, String body) {
        String rawRequestHash = exchange.getRequestHeaders().getFirst("X-Hub-Signature-256");
        if (rawRequestHash == null) return false;

        byte[] computedHash;
        try {
            computedHash = hmacUtil.hmac(body.getBytes());
        } catch (IllegalStateException e) {
            throw new RuntimeException("Error while computing hash", e);
        }

        byte[] requestHash;
        try {
            requestHash = Hex.decodeHex(rawRequestHash.substring("sha256=".length()));
        } catch (DecoderException e) {
            throw new RuntimeException("Error while decoding request signare hash", e);
        }

        return MessageDigest.isEqual(computedHash, requestHash);
    }

    private void handleRelease(String httpBody) throws IOException {
        ReleasePayload payload = objectMapper.readValue(httpBody, ReleasePayload.class);
        if (!payload.action().equals("published")) return;

        String msg = ReleaseUtil.buildReleaseMessage(payload.release());
        for (TextChannel channel : config.retrieveReleaseChannels()) {
            channel.sendMessage(msg)
                    .setSuppressEmbeds(true)
                    .queue();
        }
    }

    private void sendOk(HttpExchange exchange) throws IOException {
        byte[] bytes = "Ok".getBytes();
        exchange.sendResponseHeaders(200, 0);

        try(OutputStream stream = exchange.getResponseBody()) {
            stream.write(bytes);
        }
    }
}
