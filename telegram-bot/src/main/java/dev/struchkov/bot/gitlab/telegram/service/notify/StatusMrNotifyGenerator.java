package dev.struchkov.bot.gitlab.telegram.service.notify;

import dev.struchkov.bot.gitlab.context.domain.notify.mergerequest.StatusMrNotify;
import dev.struchkov.bot.gitlab.context.utils.Icons;
import dev.struchkov.godfather.simple.domain.BoxAnswer;
import org.springframework.stereotype.Component;

import static dev.struchkov.godfather.simple.domain.BoxAnswer.boxAnswer;
import static dev.struchkov.haiti.utils.Strings.escapeMarkdown;

@Component
public class StatusMrNotifyGenerator implements NotifyBoxAnswerGenerator<StatusMrNotify> {

    @Override
    public BoxAnswer generate(StatusMrNotify notify) {

        final StringBuilder builder = new StringBuilder(Icons.PEN).append(" *MergeRequest status changed*")
                .append(Icons.HR)
                .append(escapeMarkdown(notify.getTitle()))
                .append(Icons.HR)
                .append(notify.getOldStatus().name()).append(Icons.ARROW).append(notify.getNewStatus().name());

        final String notifyMessage = builder.toString();

        return boxAnswer(notifyMessage);
    }

    @Override
    public String getNotifyType() {
        return StatusMrNotify.TYPE;
    }

}
