package org.sadtech.bot.gitlab.teamcity.core.domain.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.sadtech.haiti.context.domain.BasicEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
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
@Table(name = "teamcity_project")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class TeamcityProject implements BasicEntity<String> {

    @Id
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "url")
    private String url;

}
