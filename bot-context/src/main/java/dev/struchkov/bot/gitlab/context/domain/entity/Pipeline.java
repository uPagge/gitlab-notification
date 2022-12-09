package dev.struchkov.bot.gitlab.context.domain.entity;

import dev.struchkov.bot.gitlab.context.domain.PipelineStatus;
import dev.struchkov.haiti.utils.fieldconstants.annotation.FieldNames;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
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

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "person_id")
    private Person person;

}
