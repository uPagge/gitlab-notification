package dev.struchkov.bot.gitlab.context.domain.entity;

import dev.struchkov.haiti.utils.fieldconstants.annotation.FieldNames;
import dev.struchkov.haiti.utils.fieldconstants.domain.Mode;
import lombok.EqualsAndHashCode;
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
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "person")
@FieldNames(mode = {Mode.TABLE, Mode.SIMPLE})
public class Person {

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "username")
    private String userName;

    @Column(name = "web_url")
    private String webUrl;

}
