package org.sadtech.bot.bitbucketbot.service.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.sadtech.bot.bitbucketbot.domain.change.Change;
import org.sadtech.bot.bitbucketbot.domain.change.pullrequest.NewPrChange;
import org.sadtech.bot.bitbucketbot.domain.change.pullrequest.ReviewersPrChange;
import org.sadtech.bot.bitbucketbot.domain.change.pullrequest.UpdatePrChange;
import org.sadtech.bot.bitbucketbot.domain.entity.PullRequest;
import org.sadtech.bot.bitbucketbot.domain.entity.Reviewer;
import org.sadtech.bot.bitbucketbot.domain.util.ReviewerChange;
import org.sadtech.bot.bitbucketbot.repository.ChangeRepository;
import org.sadtech.bot.bitbucketbot.service.ChangeService;
import org.sadtech.bot.bitbucketbot.service.PersonService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChangeServiceImpl implements ChangeService {

    private final ChangeRepository changeRepository;
    private final PersonService personService;

    @Override
    public NewPrChange create(@NonNull PullRequest newPullRequest) {
        return changeRepository.add(
                NewPrChange.builder()
                        .author(newPullRequest.getAuthorLogin())
                        .description(newPullRequest.getDescription())
                        .title(newPullRequest.getTitle())
                        .url(newPullRequest.getUrl())
                        .telegramIds(getReviewerTelegrams(newPullRequest.getReviewers()))
                        .build()
        );
    }

    @Override
    public UpdatePrChange createUpdatePr(@NonNull PullRequest oldPullRequest, @NonNull PullRequest newPullRequest) {
        return changeRepository.add(
                UpdatePrChange.builder()
                        .author(oldPullRequest.getAuthorLogin())
                        .name(newPullRequest.getTitle())
                        .telegramIds(getReviewerTelegrams(newPullRequest.getReviewers()))
                        .url(newPullRequest.getUrl())
                        .build()
        );
    }

    @Override
    public Change createReviewersPr(String prName, String prUrl, String authorLogin, List<ReviewerChange> reviewerChanges) {
        return changeRepository.add(
                ReviewersPrChange.builder()
                        .title(prName)
                        .url(prUrl)
                        .telegramIds(
                                personService.getAllTelegramIdByLogin(Collections.singleton(authorLogin))
                        )
                        .reviewerChanges(reviewerChanges)
                        .build()
        );
    }

    private Set<Long> getReviewerTelegrams(@NonNull List<Reviewer> reviewers) {
        return personService.getAllTelegramIdByLogin(
                reviewers.stream()
                        .map(Reviewer::getUserLogin)
                        .collect(Collectors.toSet())
        );
    }

    @Override
    public List<Change> getNew() {
        final List<Change> changes = changeRepository.getAll();
        changeRepository.deleteAll(changes);
        return changes;
    }


}
