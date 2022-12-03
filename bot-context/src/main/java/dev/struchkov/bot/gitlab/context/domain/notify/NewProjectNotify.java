package dev.struchkov.bot.gitlab.context.domain.notify;

import dev.struchkov.bot.gitlab.context.utils.Smile;
import dev.struchkov.haiti.utils.Strings;
import lombok.Builder;

import java.text.MessageFormat;

import static dev.struchkov.haiti.utils.Strings.escapeMarkdown;

/**
 * @author upagge 15.01.2021
 */
public record NewProjectNotify(
        String projectName,
        String projectUrl,
        String projectDescription,
        String authorName
) implements Notify {

    @Builder
    public NewProjectNotify {
    }

    @Override
    public String generateMessage() {
        return MessageFormat.format(
                "{0} *New project*{1}[{2}]({3}){1}{4}{5}: {6}",
                Smile.FUN.getValue(), Smile.HR.getValue(), projectName, projectUrl,
                (projectDescription != null && !"".equals(projectDescription)) ? escapeMarkdown(projectDescription) + Smile.HR : Strings.EMPTY,
                Smile.AUTHOR.getValue(), authorName
        );
    }

}
