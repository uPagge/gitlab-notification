package org.sadtech.bot.gitlab.teamcity.core.scheduler;

import lombok.RequiredArgsConstructor;
import org.sadtech.bot.gitlab.teamcity.core.service.parser.BuildShortParser;
import org.sadtech.bot.gitlab.teamcity.core.service.parser.TeamcityProjectParser;

/**
 * // TODO: 21.09.2020 Добавить описание.
 *
 * @author upagge 21.09.2020
 */
//@Component
@RequiredArgsConstructor
public class TeamcityProjectScheduler {

    private final TeamcityProjectParser projectParser;
    private final BuildShortParser buildShortParser;

//    @Scheduled(cron = "0 */1 * * * *")
//    public void parseNewProject() {
//        projectParser.parseNewProject();
//    }
//
//    @Scheduled(cron = "0 */1 * * * *")
//    public void parseNewBuilds() {
//        buildShortParser.parseNewBuilds();
//    }

}
