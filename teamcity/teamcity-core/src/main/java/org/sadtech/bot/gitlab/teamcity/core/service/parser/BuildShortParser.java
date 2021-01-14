package org.sadtech.bot.gitlab.teamcity.core.service.parser;

/**
 * // TODO: 21.09.2020 Добавить описание.
 *
 * @author upagge 21.09.2020
 */

import lombok.RequiredArgsConstructor;
import org.sadtech.bot.gitlab.teamcity.core.config.property.TeamcityProperty;
import org.sadtech.bot.gitlab.teamcity.core.domain.entity.BuildShort;
import org.sadtech.bot.gitlab.teamcity.core.domain.entity.TeamcityProject;
import org.sadtech.bot.gitlab.teamcity.core.service.BuildShortService;
import org.sadtech.bot.gitlab.teamcity.core.service.TeamcityProjectService;
import org.sadtech.bot.gitlab.teamcity.sdk.BuildShortJson;
import org.sadtech.haiti.context.page.Sheet;
import org.sadtech.haiti.core.page.PaginationImpl;
import org.sadtech.haiti.utils.network.HttpHeader;
import org.sadtech.haiti.utils.network.HttpParse;
import org.springframework.core.convert.ConversionService;

import java.text.MessageFormat;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.sadtech.haiti.utils.network.HttpParse.ACCEPT;
import static org.sadtech.haiti.utils.network.HttpParse.AUTHORIZATION;
import static org.sadtech.haiti.utils.network.HttpParse.BEARER;

//@Service
@RequiredArgsConstructor
public class BuildShortParser {

    private final BuildShortService buildShortService;
    private final TeamcityProjectService projectService;
    private final ConversionService conversionService;
    private final TeamcityProperty teamcityProperty;

    public void parseNewBuilds() {
        final Sheet<TeamcityProject> projectSheet = projectService.getAll(PaginationImpl.of(0, 100));
        if (projectSheet.hasContent()) {
            projectSheet.getContent().forEach(this::parse);
        }
    }

    private void parse(TeamcityProject project) {
        final List<BuildShortJson> buildShortJsons = HttpParse.request(MessageFormat.format(teamcityProperty.getBuildUrl(), project.getId()))
                .header(ACCEPT)
                .header(HttpHeader.of(AUTHORIZATION, BEARER + teamcityProperty.getToken()))
                .executeList(BuildShortJson.class);
        if (!buildShortJsons.isEmpty()) {
            final Set<Long> buildIds = buildShortJsons.stream()
                    .map(BuildShortJson::getId)
                    .collect(Collectors.toSet());
            final Set<Long> existsId = buildShortService.exists(buildIds);
            final List<BuildShort> buildShorts = buildShortJsons.stream()
                    .filter(json -> !existsId.contains(json.getId()))
                    .map(json -> conversionService.convert(json, BuildShort.class))
                    .peek(
                            buildShort -> buildShort.setProjectId(project.getId())
                    )
                    .collect(Collectors.toList());
            buildShortService.createAll(buildShorts);
        }
    }

}
