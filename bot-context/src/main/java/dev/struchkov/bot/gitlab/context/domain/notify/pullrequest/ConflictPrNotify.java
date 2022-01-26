package dev.struchkov.bot.gitlab.context.domain.notify.pullrequest;

import lombok.Builder;
import lombok.Getter;
import dev.struchkov.bot.gitlab.context.service.AppSettingService;
import dev.struchkov.bot.gitlab.context.utils.Smile;

@Getter
public class ConflictPrNotify extends PrNotify {

    private final String sourceBranch;

    @Builder
    private ConflictPrNotify(
            String name,
            String url,
            String projectKey,
            String sourceBranch
    ) {
        super(projectKey, name, url);
        this.sourceBranch = sourceBranch;
    }

    @Override
    public String generateMessage(AppSettingService settingService) {
        return settingService.getMessage(
                "notify.pr.conflict",
                Smile.DANGEROUS.getValue(), Smile.HR.getValue(), title, url, projectName, sourceBranch
        );
    }

}
