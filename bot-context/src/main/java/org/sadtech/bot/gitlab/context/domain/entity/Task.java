package org.sadtech.bot.gitlab.context.domain.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@Getter
@Setter
@DiscriminatorValue("true")
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class Task extends Note {

    @Column(name = "resolved")
    private Boolean resolved;

    @ManyToOne
    @JoinColumn(name = "resolved_id")
    private Person resolvedBy;

    @ManyToOne
    @JoinColumn(name = "responsible_id")
    private Person responsible;

}
