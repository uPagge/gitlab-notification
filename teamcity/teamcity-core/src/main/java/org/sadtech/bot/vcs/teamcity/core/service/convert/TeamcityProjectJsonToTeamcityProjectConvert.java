package org.sadtech.bot.vcs.teamcity.core.service.convert;

import org.sadtech.bot.vcs.teamcity.core.domain.entity.TeamcityProject;
import org.sadtech.bot.vcs.teamcity.sdk.TeamcityProjectJson;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * // TODO: 21.09.2020 Добавить описание.
 *
 * @author upagge 21.09.2020
 */
@Component
public class TeamcityProjectJsonToTeamcityProjectConvert implements Converter<TeamcityProjectJson, TeamcityProject> {

    @Override
    public TeamcityProject convert(TeamcityProjectJson source) {
        final TeamcityProject teamcityProject = new TeamcityProject();
        teamcityProject.setId(source.getId());
        teamcityProject.setDescription(source.getDescription());
        teamcityProject.setName(source.getName());
        teamcityProject.setUrl(source.getWebUrl());
        return teamcityProject;
    }

}
