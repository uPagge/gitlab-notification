package org.sadtech.bot.gitlab.teamcity.core.domain.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.sadtech.bot.gitlab.context.domain.EntityType;
import org.sadtech.haiti.context.domain.BasicEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * // TODO: 21.09.2020 Добавить описание.
 *
 * @author upagge 21.09.2020
 */
@Getter
@Setter
@Entity
@Table(name = "teamcity_setting")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class TeamcitySetting implements BasicEntity<Long> {

    @Id
    @Column(name = "id")
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "recipient_id")
    private String recipientId;

    @Enumerated(EnumType.STRING)
    @Column(name = "recipient_type")
    private EntityType recipientType;

    @Column(name = "project_id")
    private String projectId;

    @Column(name = "build_type_id")
    private String buildTypeId;

    @Column(name = "success")
    private boolean success;

    @Column(name = "failure")
    private boolean failure;

}
