package dev.struchkov.bot.gitlab.context.domain.notify.mergerequest;

import dev.struchkov.bot.gitlab.context.domain.MergeRequestState;
import dev.struchkov.bot.gitlab.context.utils.Smile;
import lombok.Builder;
import lombok.Getter;

import java.text.MessageFormat;

@Getter
public class StatusPrNotify extends PrNotify {

    private final MergeRequestState oldStatus;
    private final MergeRequestState newStatus;

    @Builder
    private StatusPrNotify(
            String name,
            String url,
            String projectName,
            MergeRequestState oldStatus,
            MergeRequestState newStatus
    ) {
        super(projectName, name, url);
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
    }

    @Override
    public String generateMessage() {
        return MessageFormat.format(
                "{0} *MergeRequest status changed | {7}*{1}[{2}]({3}){1}{4} {5} {6}",
                Smile.PEN.getValue(), Smile.HR.getValue(), title, url, oldStatus.name(), Smile.ARROW.getValue(), newStatus.name(), projectName
        );
    }

}
