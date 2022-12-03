package dev.struchkov.bot.gitlab.context.domain.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author upagge 14.01.2021
 */
@Entity
@Getter
@Setter
@Table(name = "person")
public class Person {

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
