package dev.struchkov.bot.gitlab.context.domain.notify;

import dev.struchkov.bot.gitlab.context.domain.entity.MergeRequest;
import dev.struchkov.bot.gitlab.context.service.AppSettingService;
import dev.struchkov.bot.gitlab.context.utils.MessageUtils;
import dev.struchkov.bot.gitlab.context.utils.Smile;
import lombok.Builder;

import java.util.List;

/**
 * // TODO: 20.09.2020 Добавить описание.
 *
 * @author upagge 20.09.2020
 */
//TODO [28.01.2022]: Решить доработать и оставить или удалить.
public record GoodMorningNotify(
        List<MergeRequest> mergeRequestsReviews,
        List<MergeRequest> mergeRequestsNeedWork,
        String personName, String version
) implements Notify {

    private static final Integer PR_COUNT = 4;

    @Builder
    public GoodMorningNotify {
    }

    @Override
    public String generateMessage() {
        final StringBuilder message = new StringBuilder().append(Smile.SUN).append(" *Доброе утро, ").append(personName).append("* ").append(Smile.SUN).append(Smile.TWO_BR);
        if (!mergeRequestsReviews.isEmpty()) {
            message.append("Необходимо проверить ").append(mergeRequestsReviews.size()).append(" ПР:").append(Smile.BR);
            MessageUtils.pullRequestForReview(
                    mergeRequestsReviews.stream()
                            .limit(3)
                            .toList()
            ).ifPresent(message::append);
        } else {
            message.append("Поздравляю, у тебя ни одного ПР на проверку!");
        }
        MessageUtils.pullRequestForNeedWork(
                mergeRequestsNeedWork.stream()
                        .limit(3)
                        .toList()
        ).ifPresent(
                messageNeedWork -> message.append(Smile.TWO_BR)
                        .append(Smile.DANGEROUS).append(" Требуется доработать ").append(mergeRequestsNeedWork.size()).append(" ПР:").append(Smile.BR)
                        .append(messageNeedWork)
        );
        message
                .append(Smile.TWO_BR).append("Удачного дня ").append(Smile.FLOWER)
                .append(Smile.HR)
                .append("_Version ").append(version).append(" | Developer @uPagge_");
        return message.toString();
    }

}
