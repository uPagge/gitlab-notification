package dev.struchkov.bot.gitlab.telegram.service.notify;

import dev.struchkov.bot.gitlab.context.domain.notify.issue.DescriptionIssueNotify;
import dev.struchkov.bot.gitlab.context.utils.Icons;
import dev.struchkov.godfather.simple.domain.BoxAnswer;
import org.springframework.stereotype.Component;

import static dev.struchkov.bot.gitlab.context.utils.Icons.link;
import static dev.struchkov.godfather.simple.domain.BoxAnswer.boxAnswer;

/**
 * @author Dmitry Sheyko 26.01.2021
 */
@Component
public class DescriptionIssueNotifyGenerator implements NotifyBoxAnswerGenerator<DescriptionIssueNotify> {

    @Override
    public BoxAnswer generate(DescriptionIssueNotify notify) {

        final StringBuilder builder = new StringBuilder(Icons.PEN)
                .append(String.format(" *Description of %s changed | ", notify.getIssueType()))
                .append(notify.getProjectName()).append("*")
                .append(Icons.HR)
                .append(link(notify.getType(), notify.getUrl()))
                .append(Icons.HR)
                .append("new description: ")
                .append(notify.getNewDescription());

        final String notifyMessage = builder.toString();

        return boxAnswer(notifyMessage);
    }

    @Override
    public String getNotifyType() {
        return DescriptionIssueNotify.TYPE;
    }

}