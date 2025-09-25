package dev.goldmensch.jdacbot.webhook.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Release(
        String name,
        @JsonProperty("html_url")
        String htmlUrl,
        String body
) {
}
