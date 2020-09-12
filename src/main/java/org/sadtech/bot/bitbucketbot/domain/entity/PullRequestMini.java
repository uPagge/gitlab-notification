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
 * // TODO: 12.09.2020 Добавить описание.
 *
 * @author upagge 12.09.2020
 */
@Getter
@Setter
@Entity
@Table(name = "pull_request")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PullRequestMini {

    /**
     * Идентификатор
     */
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    /**
     * Адрес ПР
     */
    @Column(name = "url")
    private String url;

    /**
     * Автор ПР
     */
    @Column(name = "author_login")
    private String authorLogin;

}
