package dev.struchkov.bot.gitlab.sdk.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Dmitry Sheyko [17.01.2023]
 */

public enum IssueStateJson {
    @JsonProperty("opened")
    OPENED,

    @JsonProperty("closed")
    CLOSED
}