package dev.struchkov.bot.gitlab.context.domain.notify.pullrequest;

import dev.struchkov.bot.gitlab.context.utils.Smile;
import lombok.Builder;
import lombok.Getter;

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
        final StringBuilder builder = new StringBuilder(Smile.UPDATE.getValue()).append(" *MergeRequest update | ").append(projectName).append("*")
                .append(Smile.HR.getValue())
                .append("[").append(title).append("](").append(url).append(")");


        if (allTasks > 0) {
            builder.append(Smile.HR.getValue())
                    .append("All tasks: ").append(allResolvedTasks).append("/").append(allTasks);

            if (personTasks > 0) {
                builder.append("\nYour tasks: ").append(personResolvedTasks).append("/").append(personTasks);
            }
        }

        builder.append(Smile.HR.getValue())
                .append(Smile.AUTHOR.getValue()).append(": ").append(author);

        return builder.toString();
    }

}
