package org.sadtech.bot.vsc.bitbucketbot.context.domain.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.sadtech.bot.vsc.context.domain.ReviewerStatus;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * Ревьювер пулреквеста.
 *
 * @author upagge [01.02.2020]
 */
@Entity
@Getter
@Setter
@Table(name = "reviewer")
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
    @Column(name = "person_login")
    private String personLogin;

    /**
     * Статус
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ReviewerStatus status;

    @Column(name = "date_change")
    private LocalDateTime dateChange;

    @Column(name = "date_smart_notify")
    private LocalDateTime dateSmartNotify;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH, optional = false)
    @JoinColumn(name = "pull_request_id")
    private PullRequest pullRequest;

}
