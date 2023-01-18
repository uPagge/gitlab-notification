package dev.struchkov.bot.gitlab.sdk.domain;

import lombok.Data;

/**
 * @author Dmitry Sheyko [17.01.2023]
 */

@Data
public class TimeStatsJson {
    private Integer timeEstimate;
    private Integer totalTimeSpent;  // количество секунд затраченых на работы, пример 37800"
    private String humanTimeEstimate;
    private String humanTotalTimeSpent; // Время строкой, пример "10h 30m"
}