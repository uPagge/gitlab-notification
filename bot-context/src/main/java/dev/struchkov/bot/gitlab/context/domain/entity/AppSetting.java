package dev.struchkov.bot.gitlab.context.domain.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Основные настройки приложения.
 *
 * @author upagge 16.01.2021
 */
@Entity
@Getter
@Setter
@Table(name = "app_setting")
public class AppSetting {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "first_start")
    private boolean firstStart;

    @Column(name = "enable_notify")
    private boolean enableNotify;

}
