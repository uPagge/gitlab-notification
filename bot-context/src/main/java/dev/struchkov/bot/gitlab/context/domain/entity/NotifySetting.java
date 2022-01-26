package dev.struchkov.bot.gitlab.context.domain.entity;

import dev.struchkov.haiti.context.domain.BasicEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Id;
import java.time.LocalDateTime;

/**
 * // TODO: 20.09.2020 Добавить описание.
 *
 * @author upagge 20.09.2020
 */
@Getter
@Setter
//@Entity
//@Table(name = "setting_notify")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class NotifySetting implements BasicEntity<String> {

    @Id
    @Column(name = "login")
    @EqualsAndHashCode.Include
    private String id;

    /**
     * Дата, после которой пользователю будут поступать уведомления.
     */
    @Column(name = "start_receiving")
    private LocalDateTime startReceiving;

}
