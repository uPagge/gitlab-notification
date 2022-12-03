package dev.struchkov.bot.gitlab.context.domain.notify.pullrequest;

import dev.struchkov.bot.gitlab.context.utils.Smile;
import lombok.Builder;
import lombok.Getter;

import java.text.MessageFormat;

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
    public String generateMessage() {
        return MessageFormat.format(
                "{0} *MergeRequest update | {6}*{3}[{1}]({2}){3}{4}: {5}",
                Smile.UPDATE.getValue(), title, url, Smile.HR.getValue(), Smile.AUTHOR.getValue(), author, projectName, allTasks, allResolvedTasks, personTasks, personResolvedTasks
        );
    }

}
