package dev.struchkov.bot.gitlab.telegram.service.notify;

import dev.struchkov.bot.gitlab.context.domain.notify.mergerequest.UpdateMrNotify;
import dev.struchkov.bot.gitlab.context.utils.Icons;
import dev.struchkov.bot.gitlab.context.utils.Smile;
import dev.struchkov.godfather.main.domain.BoxAnswer;
import org.springframework.stereotype.Component;

import static dev.struchkov.bot.gitlab.context.utils.Icons.link;
import static dev.struchkov.godfather.main.domain.BoxAnswer.boxAnswer;
import static dev.struchkov.haiti.utils.Strings.escapeMarkdown;

@Component
public class UpdatePrNotifyGenerator implements NotifyBoxAnswerGenerator<UpdateMrNotify> {

    @Override
    public BoxAnswer generate(UpdateMrNotify notify) {

        final StringBuilder builder = new StringBuilder(Icons.UPDATE).append(" *MergeRequest update | ").append(escapeMarkdown(notify.getProjectName())).append("*")
                .append(Smile.HR.getValue())
                .append(link(notify.getTitle(), notify.getUrl()));

        if (notify.getAllTasks() > 0) {
            builder.append(Smile.HR.getValue())
                    .append("All tasks: ").append(notify.getAllResolvedTasks()).append("/").append(notify.getAllTasks());

            if (notify.getPersonTasks() > 0) {
                builder.append("\nYour tasks: ").append(notify.getPersonResolvedTasks()).append("/").append(notify.getPersonTasks());
            }
        }

        builder.append(Icons.HR)
                .append(Icons.AUTHOR).append(": ").append(notify.getAuthor());

        final String notifyMessage = builder.toString();
        return boxAnswer(notifyMessage);
    }

    @Override
    public String getNotifyType() {
        return UpdateMrNotify.TYPE;
    }

}
