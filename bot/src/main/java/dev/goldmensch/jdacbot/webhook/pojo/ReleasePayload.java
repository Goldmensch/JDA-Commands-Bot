package dev.goldmensch.jdacbot.webhook.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ReleasePayload(
        String action,
        Release release
) {
}
