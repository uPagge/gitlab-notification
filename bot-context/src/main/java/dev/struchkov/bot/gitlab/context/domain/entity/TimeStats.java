package dev.struchkov.bot.gitlab.context.domain.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;

/**
 * Сущность TimeStats.
 *
 * @author Dmitry Sheyko [17.01.2023]
 */

@Embeddable
@Getter
@Setter
public class TimeStats {
    private Integer timeEstimate;
    private Integer totalTimeSpent;  // количество секунд затраченых на работы, пример 37800"
    private String humanTimeEstimate;
    private String humanTotalTimeSpent; // Время строкой, пример "10h 30m"
}

