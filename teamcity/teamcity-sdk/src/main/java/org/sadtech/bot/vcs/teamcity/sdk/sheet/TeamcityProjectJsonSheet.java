package org.sadtech.bot.vcs.teamcity.sdk.sheet;

import lombok.Setter;
import org.sadtech.bot.vcs.teamcity.sdk.Sheet;
import org.sadtech.bot.vcs.teamcity.sdk.TeamcityProjectJson;

import java.util.List;

/**
 * // TODO: 21.09.2020 Добавить описание.
 *
 * @author upagge 21.09.2020
 */
@Setter
public class TeamcityProjectJsonSheet extends Sheet<TeamcityProjectJson> {

    private List<TeamcityProjectJson> project;

    @Override
    public List<TeamcityProjectJson> getContent() {
        return project;
    }

}
