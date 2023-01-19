package dev.struchkov.bot.gitlab.sdk.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author Dmitry Sheyko [17.01.2023]
 */

@Data
public class TimeStatsJson {

    @JsonProperty("time_estimate")
    private Integer timeEstimate;

    @JsonProperty("total_time_spent")
    private Integer totalTimeSpent;  // количество секунд затраченых на работы, пример 37800"

    @JsonProperty("human_time_estimate")
    private String humanTimeEstimate;

    @JsonProperty("human_total_time_spent")
    private String humanTotalTimeSpent; // Время строкой, пример "10h 30m"
}