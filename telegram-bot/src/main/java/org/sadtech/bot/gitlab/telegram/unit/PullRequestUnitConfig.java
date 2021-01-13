package org.sadtech.bot.gitlab.telegram.unit;

import org.sadtech.bot.gitlab.telegram.service.unit.pullrequest.PullRequestNeedWorkProcessing;
import org.sadtech.bot.gitlab.telegram.service.unit.pullrequest.PullRequestReviewProcessing;
import org.sadtech.social.bot.domain.unit.AnswerProcessing;
import org.sadtech.social.bot.domain.unit.AnswerText;
import org.sadtech.social.core.domain.BoxAnswer;
import org.sadtech.social.core.domain.content.Message;
import org.sadtech.social.core.utils.KeyBoards;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * // TODO: 02.10.2020 Добавить описание.
 *
 * @author upagge 02.10.2020
 */
@Configuration
public class PullRequestUnitConfig {

    @Bean
    public AnswerText menuPullRequest(
            AnswerProcessing<Message> reviewPullRequest,
            AnswerProcessing<Message> needWorkPullRequest
    ) {
        return AnswerText.builder()
                .boxAnswer(
                        BoxAnswer.builder()
                                .message("Эта вкладка для работы с Pull Request")
                                .keyBoard(
                                        KeyBoards.verticalMenuString(
                                                "Нуждаются в ревью",
                                                "Необходимо доработать"
                                        )
                                )
                                .build()
                )
                .phrase("Pull Requests")
                .nextUnit(reviewPullRequest)
                .nextUnit(needWorkPullRequest)
                .build();
    }

    @Bean
    public AnswerProcessing<Message> reviewPullRequest(
            PullRequestReviewProcessing pullRequestReviewProcessing
    ) {
        return AnswerProcessing.builder()
                .processingData(pullRequestReviewProcessing)
                .phrase("Нуждаются в ревью")
                .build();
    }

    @Bean
    public AnswerProcessing<Message> needWorkPullRequest(
            PullRequestNeedWorkProcessing pullRequestNeedWorkProcessing
    ) {
        return AnswerProcessing.builder()
                .processingData(pullRequestNeedWorkProcessing)
                .phrase("Необходимо доработать")
                .build();
    }

}
