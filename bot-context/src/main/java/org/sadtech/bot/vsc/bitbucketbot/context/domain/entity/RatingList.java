package org.sadtech.bot.vsc.bitbucketbot.context.domain.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * // TODO: 01.10.2020 Добавить описание.
 *
 * @author upagge 01.10.2020
 */

@Getter
@Setter
@Entity
@Table(name = "rating_list")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class RatingList implements Comparable<RatingList> {

    @Id
    @Column(name = "login")
    @EqualsAndHashCode.Include
    private String login;

    @Column(name = "points")
    private Integer points;

    @Column(name = "number")
    private Integer number;

    @Override
    public int compareTo(@NonNull RatingList ratingList) {
        return Integer.compare(ratingList.getPoints(), points);
    }

}
