package com.tsc.bitbucketbot.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * TODO: Добавить описание класса.
 *
 * @author upagge [30.01.2020]
 */
@Builder
@Getter
@Setter
@Entity
@Table(name = "`user`")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "login")
@ToString
public class User {

    @Id
    @Column(name = "login")
    private String login;

    @Column(name = "token")
    private String token;

    @Column(name = "telegram_id")
    private Long telegramId;

}
