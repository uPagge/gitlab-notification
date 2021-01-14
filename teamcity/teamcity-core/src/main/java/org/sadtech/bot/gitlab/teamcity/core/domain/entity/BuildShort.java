package org.sadtech.bot.gitlab.teamcity.core.domain.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.sadtech.bot.gitlab.teamcity.sdk.BuildState;
import org.sadtech.bot.gitlab.teamcity.sdk.BuildStatus;
import org.sadtech.haiti.context.domain.BasicEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
@Table(name = "teamcity_build")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class BuildShort implements BasicEntity<Long> {

    @Id
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "project_id")
    private String projectId;

    @Column(name = "number")
    private Integer number;

    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    private BuildState state;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private BuildStatus status;

    @Column(name = "branch_name")
    private String branchName;

    @Column(name = "build_type_id")
    private String buildTypeId;

    @Column(name = "api_url")
    private String apiUrl;

    @Column(name = "url")
    private String url;

}
