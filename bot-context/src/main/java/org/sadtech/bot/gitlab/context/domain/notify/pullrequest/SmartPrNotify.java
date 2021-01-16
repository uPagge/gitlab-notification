package org.sadtech.bot.gitlab.context.domain.notify.pullrequest;

import lombok.Builder;
import lombok.Getter;
import org.sadtech.bot.gitlab.context.domain.entity.Reviewer;
import org.sadtech.bot.gitlab.context.service.AppSettingService;
import org.sadtech.bot.gitlab.context.utils.Smile;

/**
 * // TODO: 11.10.2020 Добавить описание.
 *
 * @author upagge 11.10.2020
 */
@Getter
public class SmartPrNotify extends PrNotify {

    private final Reviewer reviewerTriggered;

    @Builder
    protected SmartPrNotify(
            String title,
            String url,
            String projectName,
            String repositorySlug,
            Reviewer reviewerTriggered
    ) {
        super(projectName, title, url);
        this.reviewerTriggered = reviewerTriggered;
    }

    @Override
    public String generateMessage(AppSettingService settingService) {
        return settingService.getMessage(
                "notify.pr.smart",
                Smile.SMART.getValue(), title, url, Smile.HR.getValue(), reviewerTriggered.getPersonLogin(),
                projectName
        );
    }

}
