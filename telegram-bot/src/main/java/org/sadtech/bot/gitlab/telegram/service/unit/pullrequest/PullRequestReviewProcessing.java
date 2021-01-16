package org.sadtech.bot.gitlab.telegram.service.unit.pullrequest;

import lombok.RequiredArgsConstructor;
import org.sadtech.bot.gitlab.context.service.MergeRequestsService;
import org.sadtech.social.core.domain.BoxAnswer;
import org.sadtech.social.core.domain.ProcessingData;
import org.sadtech.social.core.domain.content.Message;

/**
 * // TODO: 17.09.2020 Добавить описание.
 *
 * @author upagge 17.09.2020
 */
//@Component
@RequiredArgsConstructor
public class PullRequestReviewProcessing implements ProcessingData<Message> {

    private final MergeRequestsService mergeRequestsService;

    @Override
    public BoxAnswer processing(Message message) {
//        final Person person = personService.getByTelegramId(message.getPersonId())
//                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
//        final List<PullRequest> pullRequests = pullRequestsService.getAllByReviewerAndStatuses(
//                person.getLogin(),
//                ReviewerStatus.NEEDS_WORK,
//                Collections.singleton(PullRequestStatus.OPEN)
//        );
//        return BoxAnswer.of(
//                MessageUtils.pullRequestForReview(pullRequests)
//                        .orElse("Все ПР проверены :)")
//        );
        return null;
    }

}
