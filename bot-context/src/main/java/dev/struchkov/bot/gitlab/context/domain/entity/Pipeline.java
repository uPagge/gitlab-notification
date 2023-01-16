package dev.struchkov.bot.gitlab.context.domain.entity;

import dev.struchkov.bot.gitlab.context.domain.PipelineStatus;
import dev.struchkov.haiti.utils.fieldconstants.annotation.FieldNames;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * @author upagge 17.01.2021
 */

@Entity
@Getter
@Setter
@FieldNames
@Table(name = "pipeline")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Pipeline {

    @Id
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "created_date")
    private LocalDateTime created;

    @Column(name = "updated_date")
    private LocalDateTime updated;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private PipelineStatus status;

    @Column(name = "ref")
    private String ref;

    @Column(name = "web_url")
    private String webUrl;

    @Column(name = "project_id")
    private Long projectId;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "person_id")
    private Person person;

}
