package org.sadtech.bot.gitlab.context.domain.notify.pullrequest;

import lombok.Builder;
import lombok.Getter;
import org.sadtech.bot.gitlab.context.service.AppSettingService;
import org.sadtech.bot.gitlab.context.utils.Smile;

@Getter
public class UpdatePrNotify extends PrNotify {

    private final String author;

    @Builder
    private UpdatePrNotify(
            String name,
            String url,
            String author,
            String projectKey
    ) {
        super(projectKey, name, url);
        this.author = author;
    }

    @Override
    public String generateMessage(AppSettingService settingService) {
        return settingService.getMessage(
                "notify.pr.update",
                Smile.UPDATE.getValue(), title, url, Smile.HR.getValue(), Smile.AUTHOR.getValue(), author, projectName
        );
    }

}
