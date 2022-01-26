package dev.struchkov.bot.gitlab.context.domain.notify.pullrequest;

import dev.struchkov.bot.gitlab.context.utils.Smile;
import lombok.Builder;
import lombok.Getter;
import dev.struchkov.bot.gitlab.context.service.AppSettingService;

@Getter
public class UpdatePrNotify extends PrNotify {

    private final String author;
    private final Long allTasks;
    private final Long allResolvedTasks;
    private final Long personTasks;
    private final Long personResolvedTasks;

    @Builder
    private UpdatePrNotify(
            String name,
            String url,
            String author,
            String projectKey,
            Long allTasks,
            Long allResolvedTasks,
            Long personTasks,
            Long personResolvedTasks
    ) {
        super(projectKey, name, url);
        this.author = author;
        this.allTasks = allTasks;
        this.allResolvedTasks = allResolvedTasks;
        this.personTasks = personTasks;
        this.personResolvedTasks = personResolvedTasks;
    }

    @Override
    public String generateMessage(AppSettingService settingService) {
        return settingService.getMessage(
                "notify.pr.update",
                Smile.UPDATE.getValue(), title, url, Smile.HR.getValue(), Smile.AUTHOR.getValue(), author, projectName, allTasks, allResolvedTasks, personTasks, personResolvedTasks
        );
    }

}
