package dev.struchkov.bot.gitlab.context.domain.notify.mergerequest;

import dev.struchkov.bot.gitlab.context.utils.Smile;
import lombok.Builder;
import lombok.Getter;

import java.text.MessageFormat;

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
    public String generateMessage() {
        return MessageFormat.format(
                "{0} *Attention! MergeRequest conflict | {4}*{1}[{2}]({3})",
                Smile.DANGEROUS.getValue(), Smile.HR.getValue(), title, url, projectName, sourceBranch
        );
    }

}
