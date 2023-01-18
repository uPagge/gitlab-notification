package dev.struchkov.bot.gitlab.sdk.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author Dmitry Sheyko [17.01.2023]
 */

@Data
public class ReferencesJson {
    @JsonProperty("short")
    private String shortReference;

    @JsonProperty("relative")
    private String relativeReference;

    @JsonProperty("full")
    private String fullReference;
}
