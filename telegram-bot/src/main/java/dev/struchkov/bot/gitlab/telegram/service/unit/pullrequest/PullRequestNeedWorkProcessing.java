package dev.struchkov.bot.gitlab.telegram.service.unit.pullrequest;

import dev.struchkov.bot.gitlab.context.service.MergeRequestsService;
import dev.struchkov.godfather.context.domain.BoxAnswer;
import dev.struchkov.godfather.context.domain.content.Message;
import dev.struchkov.godfather.context.service.usercode.ProcessingData;
import lombok.RequiredArgsConstructor;

/**
 * @author upagge 17.09.2020
 */
//@Component
@RequiredArgsConstructor
public class PullRequestNeedWorkProcessing implements ProcessingData<Message> {

    private final MergeRequestsService mergeRequestsService;

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
