package org.sadtech.bot.vcs.teamcity.core.domain;

import lombok.Builder;
import lombok.Getter;
import org.sadtech.bot.vcs.core.domain.EntityType;
import org.sadtech.bot.vcs.core.domain.notify.Notify;
import org.sadtech.bot.vcs.core.utils.Smile;
import org.sadtech.bot.vcs.teamcity.core.domain.entity.BuildShort;
import org.sadtech.bot.vcs.teamcity.sdk.BuildStatus;

import java.text.MessageFormat;
import java.util.Set;

/**
 * // TODO: 21.09.2020 Добавить описание.
 *
 * @author upagge 21.09.2020
 */
@Getter
public class TeamcityBuildNotify extends Notify {

    private final BuildShort buildShort;

    @Builder
    private TeamcityBuildNotify(EntityType entityType, Set<String> recipients, BuildShort buildShort) {
        super(entityType, recipients);
        this.buildShort = buildShort;
    }

    @Override
    public String generateMessage() {
        return MessageFormat.format(
                "{0} *Сборка* | {1,number,#}:{2,number,#} | {3}" +
                        "{4} [{5}]({6}) {4}" +
                        "{7} {8}",
                Smile.BUILD,
                buildShort.getId(),
                buildShort.getNumber(),
                buildShort.getProjectId(),
                Smile.HR,
                buildShort.getBuildTypeId(),
                buildShort.getUrl(),
                getSmile(buildShort),
                buildShort.getStatus()
        );
    }

    private String getSmile(BuildShort buildShort) {
        return BuildStatus.SUCCESS.equals(buildShort.getStatus()) ? Smile.SUCCESS.getValue() : Smile.FAILURE.getValue();
    }

}
