package dev.struchkov.bot.gitlab.context.domain.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;

/**
 * Сущность Issue.
 *
 * @author Dmitry Sheyko [17.01.2023]
 */

@Embeddable
@Getter
@Setter
public class Links {
    private String self;
    private String notes;
    private String awardEmoji;
    private String project;
    private String closedAsDuplicateOf;
}