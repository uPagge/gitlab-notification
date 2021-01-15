package org.sadtech.bot.gitlab.context.domain.entity;

import lombok.Getter;
import lombok.Setter;
import org.sadtech.haiti.context.domain.BasicEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * // TODO: 14.01.2021 Добавить описание.
 *
 * @author upagge 14.01.2021
 */
@Entity
@Getter
@Setter
@Table(name = "person")
public class Person implements BasicEntity<Long> {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "username")
    private String userName;

    @Column(name = "web_url")
    private String webUrl;

}
