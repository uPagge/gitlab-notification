package dev.struchkov.bot.gitlab.context.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

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
