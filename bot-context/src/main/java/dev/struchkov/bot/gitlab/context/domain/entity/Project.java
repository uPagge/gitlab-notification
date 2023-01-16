package dev.struchkov.bot.gitlab.context.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * @author upagge 14.01.2021
 */
@Getter
@Setter
@Entity
@Table(name = "project")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Project {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "creator_id")
    private Long creatorId;

    @Column(name = "web_url")
    private String webUrl;

}
