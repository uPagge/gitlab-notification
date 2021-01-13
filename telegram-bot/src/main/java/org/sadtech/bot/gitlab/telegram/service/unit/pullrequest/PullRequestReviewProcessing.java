package org.sadtech.bot.gitlab.telegram.service.unit.pullrequest;

import lombok.RequiredArgsConstructor;
import org.sadtech.bot.gitlab.context.domain.entity.Person;
import org.sadtech.bot.gitlab.context.domain.entity.PullRequest;
import org.sadtech.bot.gitlab.context.exception.NotFoundException;
import org.sadtech.bot.gitlab.context.service.PersonService;
import org.sadtech.bot.gitlab.context.service.PullRequestsService;
import org.sadtech.bot.gitlab.context.utils.MessageUtils;
import org.sadtech.bot.vsc.context.domain.PullRequestStatus;
import org.sadtech.bot.vsc.context.domain.ReviewerStatus;
import org.sadtech.social.bot.service.usercode.ProcessingData;
import org.sadtech.social.core.domain.BoxAnswer;
import org.sadtech.social.core.domain.content.Message;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * // TODO: 17.09.2020 Добавить описание.
 *
 * @author upagge 17.09.2020
 */
@Component
@RequiredArgsConstructor
public class PullRequestReviewProcessing implements ProcessingData<Message> {

    private final PersonService personService;
    private final PullRequestsService pullRequestsService;

    @Override
    public BoxAnswer processing(Message message) {
        final Person person = personService.getByTelegramId(message.getPersonId())
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        final List<PullRequest> pullRequests = pullRequestsService.getAllByReviewerAndStatuses(
                person.getLogin(),
                ReviewerStatus.NEEDS_WORK,
                Collections.singleton(PullRequestStatus.OPEN)
        );
        return BoxAnswer.of(
                MessageUtils.pullRequestForReview(pullRequests)
                        .orElse("Все ПР проверены :)")
        );
    }

}
