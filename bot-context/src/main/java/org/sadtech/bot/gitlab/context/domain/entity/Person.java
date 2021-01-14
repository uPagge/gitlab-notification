package org.sadtech.bot.gitlab.context.domain.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;

/**
 * // TODO: 14.01.2021 Добавить описание.
 *
 * @author upagge 14.01.2021
 */
@Entity
@Getter
@Setter
public class Person {

    private Long id;

    private String name;

}
