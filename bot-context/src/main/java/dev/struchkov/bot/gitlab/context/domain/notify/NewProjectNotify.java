package dev.struchkov.bot.gitlab.context.domain.notify;

import dev.struchkov.bot.gitlab.context.service.AppSettingService;
import dev.struchkov.bot.gitlab.context.utils.Smile;
import dev.struchkov.haiti.utils.Strings;
import lombok.Builder;

import static dev.struchkov.haiti.utils.Strings.escapeMarkdown;

/**
 * // TODO: 15.01.2021 Добавить описание.
 *
 * @author upagge 15.01.2021
 */
public class NewProjectNotify implements Notify {

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
    public String generateMessage(AppSettingService settingService) {
        return settingService.getMessage(
                "notify.project.new",
                Smile.FUN.getValue(), Smile.HR.getValue(), projectName, projectUrl,
                (projectDescription != null && !"".equals(projectDescription)) ? escapeMarkdown(projectDescription) + Smile.HR : Strings.EMPTY,
                Smile.AUTHOR.getValue(), authorName
        );
    }

}
