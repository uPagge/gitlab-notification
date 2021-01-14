package org.sadtech.bot.gitlab.context.domain.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.sadtech.haiti.context.domain.BasicEntity;

import javax.persistence.Column;
import javax.persistence.Id;

/**
 * // TODO: 11.10.2020 Добавить описание.
 *
 * @author upagge 11.10.2020
 */
@Getter
@Setter
//@Entity
//@Table(name = "chat")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Chat implements BasicEntity<String> {

    @Id
    @Column(name = "key")
    @EqualsAndHashCode.Include
    private String id;

    @Column(name = "telegram_id")
    private Long telegramId;

}
