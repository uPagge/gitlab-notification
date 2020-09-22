package org.sadtech.bot.vcs.teamcity.sdk.sheet;

import lombok.Setter;
import org.sadtech.bot.vcs.teamcity.sdk.BuildShortJson;
import org.sadtech.bot.vcs.teamcity.sdk.Sheet;

import java.util.List;

/**
 * // TODO: 21.09.2020 Добавить описание.
 *
 * @author upagge 21.09.2020
 */
@Setter
public class BuildShortJsonSheet extends Sheet<BuildShortJson> {

    private List<BuildShortJson> build;

    @Override
    public List<BuildShortJson> getContent() {
        return build;
    }

}
