package org.sadtech.bot.gitlab.context.domain.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.sadtech.haiti.context.domain.BasicEntity;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "note")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type")
@DiscriminatorValue("null")
public class Note implements BasicEntity<Long> {

    @Id
    @Column
    private Long id;

    @Column(name = "type")
    private String type;

    @Column(name = "body")
    private String body;

    @Column(name = "created_date")
    private LocalDateTime created;

    @Column(name = "updated_date")
    private LocalDateTime updated;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private Person author;

    @Column(name = "system")
    private boolean system;

    @Column(name = "noteable_id")
    private Long noteableId;

    @Column(name = "noteable_type")
    private String noteableType;

    @Column(name = "resolveable")
    private Boolean resolveable;

    @Column(name = "resolved")
    private Boolean resolved;

    @ManyToOne
    @JoinColumn(name = "resolved_id")
    private Person resolvedBy;

    @Column(name = "noteable_iid")
    private Long noteableIid;

}
