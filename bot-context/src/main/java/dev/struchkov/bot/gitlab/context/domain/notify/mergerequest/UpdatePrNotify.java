package dev.struchkov.bot.gitlab.context.domain.notify.mergerequest;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UpdatePrNotify extends PrNotify {

    public static final String TYPE = "UpdatePrNotify";

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
    public String getType() {
        return TYPE;
    }

}
