package org.sadtech.bot.gitlab.context.domain.notify;

import lombok.Builder;
import lombok.Getter;
import org.sadtech.bot.gitlab.context.domain.EntityType;
import org.sadtech.bot.gitlab.context.domain.entity.MergeRequest;
import org.sadtech.bot.gitlab.context.utils.MessageUtils;
import org.sadtech.bot.gitlab.context.utils.Smile;
import org.sadtech.bot.gitlab.context.utils.UpdateDataComparator;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * // TODO: 20.09.2020 Добавить описание.
 *
 * @author upagge 20.09.2020
 */
@Getter
public class GoodMorningNotify extends Notify {

    private static final UpdateDataComparator COMPARATOR = new UpdateDataComparator();
    private static final Integer PR_COUNT = 4;

    private final List<MergeRequest> mergeRequestsReviews;
    private final List<MergeRequest> mergeRequestsNeedWork;
    private final String personName;
    private final String version;

    @Builder
    protected GoodMorningNotify(
            Set<String> recipients,
            List<MergeRequest> mergeRequestsReviews,
            List<MergeRequest> mergeRequestsNeedWork,
            String personName, String version) {
        super(EntityType.PERSON, recipients);
        this.mergeRequestsReviews = mergeRequestsReviews;
        this.mergeRequestsNeedWork = mergeRequestsNeedWork;
        this.personName = personName;
        this.version = version;
    }

    @Override
    public String generateMessage() {
        StringBuilder message = new StringBuilder().append(Smile.SUN).append(" *Доброе утро, ").append(personName).append("* ").append(Smile.SUN).append(Smile.TWO_BR);
        if (!mergeRequestsReviews.isEmpty()) {
            message.append("Необходимо проверить ").append(mergeRequestsReviews.size()).append(" ПР:").append(Smile.BR);
            MessageUtils.pullRequestForReview(
                    mergeRequestsReviews.stream()
                            .limit(3)
                            .collect(Collectors.toList())
            ).ifPresent(message::append);
        } else {
            message.append("Поздравляю, у тебя ни одного ПР на проверку!");
        }
        MessageUtils.pullRequestForNeedWork(
                mergeRequestsNeedWork.stream()
                        .limit(3)
                        .collect(Collectors.toList())
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
