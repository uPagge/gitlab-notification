package org.sadtech.bot.bitbucketbot.domain.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Пользователь битбакета.
 *
 * @author upagge [30.01.2020]
 */
@Getter
@Setter
@Entity
@Table(name = "person")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Person {

    /**
     * Логин
     */
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    /**
     * Логин
     */
    @Column(name = "login")
    private String login;

    /**
     * Персональный токен из битбакета
     */
    @Column(name = "bitbucket_token")
    private String token;

    /**
     * Идентификатор телеграма
     */
    @Column(name = "telegram_id")
    private Long telegramId;

    /**
     * ФИО
     */
    @Column(name = "full_name")
    private String fullName;

}
