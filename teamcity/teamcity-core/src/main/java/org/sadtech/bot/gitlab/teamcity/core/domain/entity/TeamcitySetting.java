package org.sadtech.bot.gitlab.teamcity.core.domain.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.sadtech.haiti.context.domain.BasicEntity;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * // TODO: 21.09.2020 Добавить описание.
 *
 * @author upagge 21.09.2020
 */
@Getter
@Setter
//@Entity
//@Table(name = "teamcity_setting")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class TeamcitySetting implements BasicEntity<Long> {

    @Id
    @Column(name = "id")
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "recipient_id")
    private String recipientId;

    @Column(name = "project_id")
    private String projectId;

    @Column(name = "build_type_id")
    private String buildTypeId;

    @Column(name = "success")
    private boolean success;

    @Column(name = "failure")
    private boolean failure;

}
