package org.sadtech.bot.gitlab.context.domain.notify;

import lombok.Builder;
import org.sadtech.bot.gitlab.context.utils.Smile;

import java.text.MessageFormat;

/**
 * // TODO: 15.01.2021 Добавить описание.
 *
 * @author upagge 15.01.2021
 */
public class NewProjectNotify extends Notify {

    private final String projectName;
    private final String projectUrl;
    private final String projectDescription;
    private final String authorName;

    @Builder
    public NewProjectNotify(String projectName, String projectUrl, String projectDescription, String authorName) {
        this.projectName = projectName;
        this.projectUrl = projectUrl;
        this.projectDescription = projectDescription;
        this.authorName = authorName;
    }

    @Override
    public String generateMessage() {
        return MessageFormat.format(
                "{0} *Новый Проект*{1}" +
                        "[{2}]({3}){1}" +
                        "{4}" +
                        "{5}: {6}\n\n",
                Smile.FUN, Smile.HR, projectName, projectUrl,
                (projectDescription != null && !"".equals(projectDescription)) ? escapeMarkdown(projectDescription) + Smile.HR : "",
                Smile.AUTHOR, authorName
        );
    }

}
