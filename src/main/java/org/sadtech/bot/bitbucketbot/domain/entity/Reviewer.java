package org.sadtech.bot.bitbucketbot.domain.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.sadtech.bot.bitbucketbot.domain.ReviewerStatus;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Ревьювер пулреквеста.
 *
 * @author upagge [01.02.2020]
 */
@Entity
@Getter
@Setter
@Table(name = "pull_request_reviewer")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Reviewer {

    /**
     * Идентификатор
     */
    @Id
    @Column(name = "id")
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Пользователь
     */
    @Column(name = "user_login")
    private String userLogin;

    /**
     * Статус
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ReviewerStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    private PullRequest pullRequest;

}
