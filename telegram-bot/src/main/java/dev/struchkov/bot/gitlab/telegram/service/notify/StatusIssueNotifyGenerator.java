package dev.struchkov.bot.gitlab.telegram.service.notify;

import dev.struchkov.bot.gitlab.context.domain.notify.issue.StatusIssueNotify;
import dev.struchkov.bot.gitlab.context.utils.Icons;
import dev.struchkov.godfather.simple.domain.BoxAnswer;
import org.springframework.stereotype.Component;

import static dev.struchkov.bot.gitlab.context.utils.Icons.link;
import static dev.struchkov.godfather.simple.domain.BoxAnswer.boxAnswer;

/**
 * @author Dmitry Sheyko 26.01.2021
 */
@Component
public class StatusIssueNotifyGenerator implements NotifyBoxAnswerGenerator<StatusIssueNotify> {

    @Override
    public BoxAnswer generate(StatusIssueNotify notify) {
        final StringBuilder builder = new StringBuilder(Icons.PEN)
                .append(String.format(" *Status of %s changed | ", notify.getIssueType()))
                .append(notify.getProjectName()).append("*")
                .append(Icons.HR)
                .append(link(notify.getType(), notify.getUrl()))
                .append(Icons.HR)
                .append(notify.getOldStatus().name()).append(Icons.ARROW).append(notify.getNewStatus().name());


        final String notifyMessage = builder.toString();

        return boxAnswer(notifyMessage);
    }

    @Override
    public String getNotifyType() {
        return StatusIssueNotify.TYPE;
    }

}