package org.sadtech.bot.vcs.core.domain.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * // TODO: 11.10.2020 Добавить описание.
 *
 * @author upagge 11.10.2020
 */
@Getter
@Setter
@Entity
@Table(name = "chat")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Chat {

    @Id
    @Column(name = "key")
    @EqualsAndHashCode.Include
    private String key;

    @Column(name = "telegram_id")
    private Long telegramId;

}
