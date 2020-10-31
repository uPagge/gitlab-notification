package org.sadtech.bot.vsc.bitbucketbot.context.domain.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * // TODO: 20.09.2020 Добавить описание.
 *
 * @author upagge 20.09.2020
 */
@Getter
@Setter
@Entity
@Table(name = "setting_notify")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class NotifySetting {

    /**
     * Логин пользователя, которому принадлежат настройки
     */
    @Id
    @Column(name = "login")
    @EqualsAndHashCode.Include
    private String login;

    /**
     * Дата, после которой пользователю будут поступать уведомления.
     */
    @Column(name = "start_receiving")
    private LocalDateTime startReceiving;

}
