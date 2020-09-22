package org.sadtech.bot.vcs.teamcity.core.service.parser;

/**
 * // TODO: 21.09.2020 Добавить описание.
 *
 * @author upagge 21.09.2020
 */

import lombok.RequiredArgsConstructor;
import org.sadtech.basic.context.page.Sheet;
import org.sadtech.basic.core.page.PaginationImpl;
import org.sadtech.bot.vcs.core.service.Utils;
import org.sadtech.bot.vcs.teamcity.core.config.property.TeamcityProperty;
import org.sadtech.bot.vcs.teamcity.core.domain.entity.BuildShort;
import org.sadtech.bot.vcs.teamcity.core.domain.entity.TeamcityProject;
import org.sadtech.bot.vcs.teamcity.core.service.BuildShortService;
import org.sadtech.bot.vcs.teamcity.core.service.TeamcityProjectService;
import org.sadtech.bot.vcs.teamcity.sdk.BuildShortJson;
import org.sadtech.bot.vcs.teamcity.sdk.sheet.BuildShortJsonSheet;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
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
        final Optional<BuildShortJsonSheet> buildShortJsonSheet = Utils.urlToJson(
                MessageFormat.format(
                        teamcityProperty.getBuildUrl(),
                        project.getId()
                ),
                teamcityProperty.getToken(),
                BuildShortJsonSheet.class
        );
        if (buildShortJsonSheet.isPresent()) {
            final List<BuildShortJson> buildShortJsons = buildShortJsonSheet.get().getContent();
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
