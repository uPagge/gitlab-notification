package org.sadtech.bot.gitlab.sdk.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * TODO: Добавить комментарий енума.
 *
 * @author upagge [01.02.2020]
 */
public enum MergeRequestStateJson {

    @JsonProperty("opened")
    OPENED,
    @JsonProperty("closed")
    CLOSED,
    @JsonProperty("locked")
    LOCKED,
    @JsonProperty("merger")
    MERGED

}
