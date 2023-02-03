package dev.struchkov.bot.gitlab.telegram.service.notify;

import dev.struchkov.bot.gitlab.context.domain.notify.issue.TitleIssueNotify;
import dev.struchkov.bot.gitlab.context.utils.Icons;
import dev.struchkov.godfather.main.domain.BoxAnswer;
import org.springframework.stereotype.Component;

import static dev.struchkov.bot.gitlab.context.utils.Icons.link;
import static dev.struchkov.godfather.main.domain.BoxAnswer.boxAnswer;

/**
 * @author Dmitry Sheyko 26.01.2021
 */
@Component
public class TitleIssueNotifyGenerator implements NotifyBoxAnswerGenerator<TitleIssueNotify> {

    @Override
    public BoxAnswer generate(TitleIssueNotify notify) {

        final StringBuilder builder = new StringBuilder(Icons.PEN)
                .append(String.format(" *Title of %s changed | ", notify.getIssueType()))
                .append(notify.getProjectName()).append("*")
                .append(Icons.HR)
                .append(link(notify.getType(), notify.getUrl()))
                .append(Icons.HR)
                .append("new title: ")
                .append(notify.getNewTitle());

        final String notifyMessage = builder.toString();

        return boxAnswer(notifyMessage);
    }

    @Override
    public String getNotifyType() {
        return TitleIssueNotify.TYPE;
    }

}