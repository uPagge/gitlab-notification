package dev.struchkov.bot.gitlab.telegram.service.notify;

import dev.struchkov.bot.gitlab.context.domain.notify.mergerequest.StatusPrNotify;
import dev.struchkov.bot.gitlab.context.utils.Icons;
import dev.struchkov.godfather.main.domain.BoxAnswer;
import org.springframework.stereotype.Component;

import static dev.struchkov.bot.gitlab.context.utils.Icons.link;
import static dev.struchkov.godfather.main.domain.BoxAnswer.boxAnswer;

@Component
public class StatusPrNotifyGenerator implements NotifyBoxAnswerGenerator<StatusPrNotify> {

    @Override
    public BoxAnswer generate(StatusPrNotify notify) {

        final StringBuilder builder = new StringBuilder(Icons.PEN).append(" *MergeRequest status changed | ").append(notify.getProjectName()).append("*")
                .append(Icons.HR)
                .append(link(notify.getTitle(), notify.getUrl()))
                .append(Icons.HR)
                .append(notify.getOldStatus().name()).append(Icons.ARROW).append(notify.getNewStatus().name());

        final String notifyMessage = builder.toString();

        return boxAnswer(notifyMessage);
    }

    @Override
    public String getNotifyType() {
        return StatusPrNotify.TYPE;
    }

}
