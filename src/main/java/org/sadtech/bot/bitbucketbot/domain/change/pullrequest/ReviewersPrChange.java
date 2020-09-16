package org.sadtech.bot.bitbucketbot.domain.change.pullrequest;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.sadtech.bot.bitbucketbot.domain.change.ChangeType;
import org.sadtech.bot.bitbucketbot.domain.util.ReviewerChange;
import org.sadtech.bot.bitbucketbot.utils.Smile;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.sadtech.bot.bitbucketbot.domain.util.ReviewerChange.Type.DELETED;
import static org.sadtech.bot.bitbucketbot.domain.util.ReviewerChange.Type.NEW;
import static org.sadtech.bot.bitbucketbot.domain.util.ReviewerChange.Type.OLD;

@Getter
@EqualsAndHashCode(callSuper = true)
public class ReviewersPrChange extends PrChange {

    private final List<ReviewerChange> reviewerChanges;

    @Builder
    private ReviewersPrChange(
            Set<Long> telegramIds,
            String title,
            String url,
            List<ReviewerChange> reviewerChanges) {
        super(ChangeType.REVIEWERS, telegramIds, title, url);
        this.reviewerChanges = reviewerChanges;
    }

    @Override
    public String generateMessage() {
        StringBuilder stringBuilder = new StringBuilder();
        final Map<ReviewerChange.Type, List<ReviewerChange>> changes = reviewerChanges.stream()
                .collect(Collectors.groupingBy(ReviewerChange::getType));
        if (changes.containsKey(OLD)) {
            stringBuilder.append(Smile.BR).append("Изменили свое решение:").append(Smile.BR);
            changes.get(OLD).forEach(
                    change -> stringBuilder
                            .append(Smile.AUTHOR).append(change.getName()).append(": ")
                            .append(change.getOldStatus().getValue()).append(" -> ")
                            .append(change.getStatus().getValue())
                            .append(Smile.BR)
            );
        }
        if (changes.containsKey(NEW)) {
            stringBuilder.append(Smile.BR).append("Новые ревьюверы:").append(Smile.BR);
            changes.get(NEW).forEach(
                    change -> stringBuilder
                            .append(change.getName()).append(" (").append(change.getStatus().getValue()).append(")")
                            .append(Smile.BR)
            );
        }
        if (changes.containsKey(DELETED)) {
            stringBuilder.append(Smile.BR).append("Не выдержали ревью:").append(Smile.BR)
                    .append(
                            changes.get(DELETED).stream()
                                    .map(ReviewerChange::getName).collect(Collectors.joining(","))
                    );
        }
        final String createMessage = stringBuilder.toString();
        return Smile.PEN + " *Изменения ревьюверов PullRequest*" +
                Smile.HR +
                "[" + title + "](" + url + ")" + Smile.HR +
                createMessage;
    }
}
