package dev.struchkov.bot.gitlab.context.domain.entity;

import dev.struchkov.bot.gitlab.context.domain.MilestoneState;
import dev.struchkov.haiti.utils.fieldconstants.annotation.FieldNames;
import dev.struchkov.haiti.utils.fieldconstants.domain.Mode;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Сущность Milestone.
 *
 * @author Dmitry Sheyko 17.01.2023
 */

@Entity
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "milestone")
@FieldNames(mode = {Mode.TABLE, Mode.SIMPLE})
public class Milestone {

    @Id
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "two_id")
    private Long twoId;

    @Column(name = "project_id")
    private Long projectId;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "state")
    private MilestoneState state;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @Column(name = "start_date")
    private LocalDateTime startDate; //установленное создателем время начала

    @Column(name = "due_date")
    private LocalDateTime dueDate; //установленное создателем время окончания

    @Column(name = "expired")
    private Boolean expired;

    @Column(name = "web_url")
    private String webUrl;
}