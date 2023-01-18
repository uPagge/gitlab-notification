package dev.struchkov.bot.gitlab.sdk.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author Dmitry Sheyko [17.01.2023]
 */

@Data
public class LinksJson {
    private String self;
    private String notes;

    @JsonProperty("award_emoji")
    private String awardEmoji;
    private String project;

    @JsonProperty("closed_as_duplicate_of")
    private String closedAsDuplicateOf;
}