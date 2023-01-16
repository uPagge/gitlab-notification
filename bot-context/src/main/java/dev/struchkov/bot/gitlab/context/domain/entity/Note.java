package dev.struchkov.bot.gitlab.context.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import static jakarta.persistence.CascadeType.MERGE;
import static jakarta.persistence.CascadeType.PERSIST;

@Getter
@Setter
@Entity
@Table(name = "note")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Note {

    @Id
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "type")
    private String type;

    @Column(name = "body")
    private String body;

    @Column(name = "created_date")
    private LocalDateTime created;

    @Column(name = "updated_date")
    private LocalDateTime updated;

    @ManyToOne(cascade = {PERSIST, MERGE})
    @JoinColumn(name = "author_id")
    private Person author;

    @Column(name = "noteable_id")
    private Long noteableId;

    @Column(name = "noteable_type")
    private String noteableType;

    @Column(name = "noteable_iid")
    private Long noteableIid;

    @Column(name = "web_url")
    private String webUrl;

    @Column(name = "resolvable")
    private boolean resolvable;

    @Column(name = "resolved")
    private Boolean resolved;

    @ManyToOne(cascade = {PERSIST, MERGE})
    @JoinColumn(name = "resolved_id")
    private Person resolvedBy;

    @ManyToOne(optional = false)
    @JoinColumn(name = "discussion_id")
    private Discussion discussion;

}
