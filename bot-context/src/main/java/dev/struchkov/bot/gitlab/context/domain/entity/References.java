package dev.struchkov.bot.gitlab.context.domain.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;

/**
 * Сущность References.
 *
 * @author Dmitry Sheyko [17.01.2023]
 */

@Embeddable
@Getter
@Setter
public class References {
    private String shortReference;
    private String relativeReference;
    private String fullReference;
}