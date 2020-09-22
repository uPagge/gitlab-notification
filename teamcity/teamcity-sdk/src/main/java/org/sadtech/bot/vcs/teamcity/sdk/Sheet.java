package org.sadtech.bot.vcs.teamcity.sdk;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * // TODO: 21.09.2020 Добавить описание.
 *
 * @author upagge 21.09.2020
 */
@Getter
@Setter
public abstract class Sheet<T> {

    private Integer count;

    public abstract List<T> getContent();

}
