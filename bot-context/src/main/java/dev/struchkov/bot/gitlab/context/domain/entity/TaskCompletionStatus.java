package dev.struchkov.bot.gitlab.context.domain.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;

/**
 * Сущность TaskCompletionStatus.
 *
 * @author Dmitry Sheyko [17.01.2023]
 */

@Embeddable
@Getter
@Setter
public class TaskCompletionStatus {
    private Integer count;
    private Integer completedCount;
}