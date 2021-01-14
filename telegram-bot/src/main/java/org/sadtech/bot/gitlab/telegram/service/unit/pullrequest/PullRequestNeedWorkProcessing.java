package org.sadtech.bot.gitlab.telegram.service.unit.pullrequest;

import lombok.RequiredArgsConstructor;
import org.sadtech.bot.gitlab.context.service.PullRequestsService;
import org.sadtech.social.bot.service.usercode.ProcessingData;
import org.sadtech.social.core.domain.BoxAnswer;
import org.sadtech.social.core.domain.content.Message;

/**
 * // TODO: 17.09.2020 Добавить описание.
 *
 * @author upagge 17.09.2020
 */
//@Component
@RequiredArgsConstructor
public class PullRequestNeedWorkProcessing implements ProcessingData<Message> {

    private final PullRequestsService pullRequestsService;

    @Override
    public BoxAnswer processing(Message message) {
//        final Person person = personService.getByTelegramId(message.getPersonId())
//                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
//        final List<PullRequest> pullRequests = pullRequestsService.getAllByAuthorAndReviewerStatus(person.getLogin(), ReviewerStatus.UNAPPROVED);
//        return BoxAnswer.of(
//                MessageUtils.pullRequestForNeedWork(pullRequests)
//                        .orElse("Не найдено ПРов, которые нуждаются в доработке :)")
//        );
        return null;
    }

}
