package org.sadtech.bot.vcs.teamcity.core.service.parser;

import lombok.RequiredArgsConstructor;
import org.sadtech.bot.vcs.core.utils.Utils;
import org.sadtech.bot.vcs.teamcity.core.config.property.TeamcityProperty;
import org.sadtech.bot.vcs.teamcity.core.domain.entity.TeamcityProject;
import org.sadtech.bot.vcs.teamcity.core.service.TeamcityProjectService;
import org.sadtech.bot.vcs.teamcity.sdk.TeamcityProjectJson;
import org.sadtech.bot.vcs.teamcity.sdk.sheet.TeamcityProjectJsonSheet;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * // TODO: 21.09.2020 Добавить описание.
 *
 * @author upagge 21.09.2020
 */
@Component
@RequiredArgsConstructor
public class TeamcityProjectParser {

    private final TeamcityProjectService teamcityProjectService;

    private final TeamcityProperty teamcityProperty;

    private final ConversionService conversionService;

    public void parseNewProject() {
        final Optional<TeamcityProjectJsonSheet> optTeamcityProjectJsonSheet = Utils.urlToJson(
                teamcityProperty.getProjectUrl(),
                teamcityProperty.getToken(),
                TeamcityProjectJsonSheet.class
        );
        if (optTeamcityProjectJsonSheet.isPresent()) {
            final List<TeamcityProjectJson> teamcityProjectJsons = optTeamcityProjectJsonSheet.get().getContent();
            final Set<String> projectIds = teamcityProjectJsons.stream()
                    .map(TeamcityProjectJson::getId)
                    .collect(Collectors.toSet());
            final List<String> exists = teamcityProjectService.exists(projectIds);
            final List<TeamcityProject> teamcityProjects = teamcityProjectJsons.stream()
                    .filter(json -> !exists.contains(json.getId()))
                    .map(json -> conversionService.convert(json, TeamcityProject.class))
                    .collect(Collectors.toList());
            teamcityProjectService.createAll(teamcityProjects);
        }
    }

}
