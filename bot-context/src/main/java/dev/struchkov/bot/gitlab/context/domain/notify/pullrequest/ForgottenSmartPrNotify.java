package dev.struchkov.bot.gitlab.context.domain.notify.pullrequest;

import dev.struchkov.bot.gitlab.context.utils.Smile;
import lombok.Builder;
import lombok.Getter;
import dev.struchkov.bot.gitlab.context.service.AppSettingService;

/**
 * // TODO: 11.10.2020 Добавить описание.
 *
 * @author upagge 11.10.2020
 */
@Getter
public class ForgottenSmartPrNotify extends PrNotify {

    @Builder
    protected ForgottenSmartPrNotify(
            String title,
            String url,
            String projectName,
            String repositorySlug
    ) {
        super(projectName, title, url);
    }

    @Override
    public String generateMessage(AppSettingService appSettingService) {
        return appSettingService.getMessage(
                "notify.pr.forgotten",
                Smile.SMART.getValue(), title, url, Smile.HR.getValue(), projectName
        );
    }

}
