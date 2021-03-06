package org.sadtech.bot.gitlab.context.domain.entity;

import lombok.Getter;
import lombok.Setter;
import org.sadtech.bot.gitlab.context.domain.AppLocale;
import org.sadtech.haiti.context.domain.BasicEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * // TODO: 16.01.2021 Добавить описание.
 *
 * @author upagge 16.01.2021
 */
@Entity
@Getter
@Setter
@Table(name = "app_setting")
public class AppSetting implements BasicEntity<Long> {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "language")
    @Enumerated(EnumType.STRING)
    private AppLocale appLocale;

    @Column(name = "first_start")
    private boolean firstStart;

}
