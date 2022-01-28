package dev.struchkov.bot.gitlab.context.domain.notify.pipeline;

import dev.struchkov.bot.gitlab.context.domain.notify.Notify;
import dev.struchkov.bot.gitlab.context.service.AppSettingService;
import dev.struchkov.bot.gitlab.context.utils.Smile;
import lombok.Builder;

import java.text.MessageFormat;

import static dev.struchkov.haiti.utils.Strings.escapeMarkdown;

/**
 * @author upagge 17.01.2021
 */
public record PipelineNotify(
        Long pipelineId,
        String projectName,
        String refName,
        String oldStatus,
        String newStatus,
        String webUrl
) implements Notify {

    @Builder
    public PipelineNotify {
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
