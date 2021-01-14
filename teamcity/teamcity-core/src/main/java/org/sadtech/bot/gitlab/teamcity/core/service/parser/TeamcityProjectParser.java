package org.sadtech.bot.gitlab.teamcity.core.service.parser;

import lombok.RequiredArgsConstructor;
import org.sadtech.bot.gitlab.teamcity.core.config.property.TeamcityProperty;
import org.sadtech.bot.gitlab.teamcity.core.domain.entity.TeamcityProject;
import org.sadtech.bot.gitlab.teamcity.core.service.TeamcityProjectService;
import org.sadtech.bot.gitlab.teamcity.sdk.TeamcityProjectJson;
import org.sadtech.haiti.utils.network.HttpHeader;
import org.sadtech.haiti.utils.network.HttpParse;
import org.springframework.core.convert.ConversionService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.sadtech.haiti.utils.network.HttpParse.ACCEPT;
import static org.sadtech.haiti.utils.network.HttpParse.AUTHORIZATION;
import static org.sadtech.haiti.utils.network.HttpParse.BEARER;

/**
 * // TODO: 21.09.2020 Добавить описание.
 *
 * @author upagge 21.09.2020
 */
//@Component
@RequiredArgsConstructor
public class TeamcityProjectParser {

    private final TeamcityProjectService teamcityProjectService;

    private final TeamcityProperty teamcityProperty;

    private final ConversionService conversionService;

    public void parseNewProject() {

        final List<TeamcityProjectJson> teamcityProjectJsons = HttpParse.request(teamcityProperty.getProjectUrl())
                .header(ACCEPT)
                .header(HttpHeader.of(AUTHORIZATION, BEARER + teamcityProperty.getToken()))
                .executeList(TeamcityProjectJson.class);

        if (!teamcityProjectJsons.isEmpty()) {
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
