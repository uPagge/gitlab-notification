package org.sadtech.bot.gitlab.context.domain.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.sadtech.haiti.context.domain.BasicEntity;

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
public class NotifySetting extends BasicEntity<String> {

    /**
     * Дата, после которой пользователю будут поступать уведомления.
     */
    @Column(name = "start_receiving")
    private LocalDateTime startReceiving;

    @Override
    @Id
    @Column(name = "login")
    @EqualsAndHashCode.Include
    public String getId() {
        return super.getId();
    }

    @Override
    public void setId(String id) {
        super.setId(id);
    }
}
