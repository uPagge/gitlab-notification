package org.sadtech.bot.gitlab.sdk.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * // TODO: 17.01.2021 Добавить описание.
 *
 * @author upagge 17.01.2021
 */
public enum PipelineStatusJson {

    @JsonProperty("created")
    CREATED,

    @JsonProperty("waiting_for_resource")
    WAITING_FOR_RESOURCE,

    @JsonProperty("preparing")
    PREPARING,

    @JsonProperty("pending")
    PENDING,

    @JsonProperty("running")
    RUNNING,

    @JsonProperty("success")
    SUCCESS,

    @JsonProperty("failed")
    FAILED,

    @JsonProperty("canceled")
    CANCELED,

    @JsonProperty("skipped")
    SKIPPED,

    @JsonProperty("manual")
    MANUAL,

    @JsonProperty("scheduled")
    SCHEDULED

}
