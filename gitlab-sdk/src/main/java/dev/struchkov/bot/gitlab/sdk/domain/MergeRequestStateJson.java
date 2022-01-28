package dev.struchkov.bot.gitlab.sdk.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author upagge [01.02.2020]
 */
public enum MergeRequestStateJson {

    @JsonProperty("opened")
    OPENED,
    @JsonProperty("closed")
    CLOSED,
    @JsonProperty("locked")
    LOCKED,
    @JsonProperty("merged")
    MERGED

}
