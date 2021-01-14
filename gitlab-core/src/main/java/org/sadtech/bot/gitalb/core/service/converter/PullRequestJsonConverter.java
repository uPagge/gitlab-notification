package org.sadtech.bot.gitalb.core.service.converter;

import lombok.RequiredArgsConstructor;
import org.sadtech.bot.gitlab.context.domain.entity.PullRequest;
import org.sadtech.bot.gitlab.sdk.domain.MergeRequestJson;
import org.sadtech.bot.gitlab.sdk.domain.Outcome;
import org.sadtech.bot.gitlab.sdk.domain.Properties;
import org.sadtech.bot.gitlab.sdk.domain.PullRequestState;
import org.sadtech.bot.gitlab.sdk.domain.UserPullRequestStatus;
import org.sadtech.bot.vsc.context.domain.PullRequestStatus;
import org.sadtech.bot.vsc.context.domain.ReviewerStatus;
import org.sadtech.haiti.context.exception.ConvertException;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PullRequestJsonConverter implements Converter<MergeRequestJson, PullRequest> {

    public static PullRequestStatus convertPullRequestStatus(PullRequestState state) {
        switch (state) {
            case OPENED:
                return PullRequestStatus.OPEN;
            case MERGED:
                return PullRequestStatus.MERGED;
            case DECLINED:
                return PullRequestStatus.DECLINED;
            default:
                throw new ConvertException("Неподдерживаемый тип ПР");
        }
    }

    private static ReviewerStatus convertStatusReviewer(UserPullRequestStatus status) {
        switch (status) {
            case APPROVED:
                return ReviewerStatus.APPROVED;
            case NEEDS_WORK:
                return ReviewerStatus.UNAPPROVED;
            case UNAPPROVED:
                return ReviewerStatus.NEEDS_WORK;
            default:
                throw new ConvertException("Неподдерживаемый статус ревьювера");
        }
    }

    @Override
    public PullRequest convert(MergeRequestJson json) {

        final PullRequest pullRequest = new PullRequest();

        return pullRequest;
    }

    private boolean convertConflict(Properties properties) {
        return properties != null
                && properties.getMergeResult() != null
                && properties.getMergeResult().getOutcome() != null
                && Outcome.CONFLICTED.equals(properties.getMergeResult().getOutcome());
    }

}
