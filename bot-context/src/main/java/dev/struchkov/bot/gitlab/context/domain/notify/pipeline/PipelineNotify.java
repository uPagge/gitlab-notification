package dev.struchkov.bot.gitlab.context.domain.notify.pipeline;

import dev.struchkov.bot.gitlab.context.domain.notify.Notify;
import dev.struchkov.bot.gitlab.context.service.AppSettingService;
import dev.struchkov.bot.gitlab.context.utils.Smile;
import lombok.Builder;

import java.text.MessageFormat;

import static dev.struchkov.haiti.utils.Strings.escapeMarkdown;

/**
 * // TODO: 17.01.2021 Добавить описание.
 *
 * @author upagge 17.01.2021
 */
public class PipelineNotify implements Notify {

    private final Long pipelineId;
    private final String projectName;
    private final String refName;
    private final String newStatus;
    private final String webUrl;
    private String oldStatus = "n/a";

    @Builder
    public PipelineNotify(
            Long pipelineId,
            String projectName,
            String refName,
            String oldStatus,
            String newStatus,
            String webUrl
    ) {
        this.pipelineId = pipelineId;
        this.projectName = projectName;
        this.refName = refName;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.webUrl = webUrl;
    }

    @Override
    public String generateMessage(AppSettingService appSettingService) {
        return MessageFormat.format(
                appSettingService.getMessage("notify.pipeline"),
                Smile.BUILD,
                pipelineId,
                escapeMarkdown(projectName),
                Smile.HR,
                refName,
                webUrl,
                oldStatus,
                Smile.ARROW,
                newStatus
        );
    }
}
