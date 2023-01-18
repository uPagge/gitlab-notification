package dev.struchkov.bot.gitlab.sdk.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author Dmitry Sheyko [17.01.2023]
 */

@Data
public class TaskCompletionStatusJson {
    @JsonProperty("count")
    private Integer count;

    @JsonProperty("completed_count")
    private Integer completedCount;
}