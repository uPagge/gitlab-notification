package org.sadtech.bot.bitbucketbot.service.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.sadtech.bot.bitbucketbot.domain.entity.PullRequest;
import org.sadtech.bot.bitbucketbot.service.PullRequestsService;
import org.sadtech.bot.bitbucketbot.service.ReportService;
import org.sadtech.bot.bitbucketbot.utils.Smile;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportServiceSimple implements ReportService {

    private final PullRequestsService pullRequestsService;

    @Override
    public String generateReport(@NonNull String login) {
        final LocalDateTime now = LocalDateTime.now();
        final Map<LocalDate, List<PullRequest>> prByDay = pullRequestsService.getAllByAuthor(login, now.minusDays(7L), now).stream()
                .collect(Collectors.groupingBy(pullRequest -> pullRequest.getUpdateDate().toLocalDate()));
        return generateMessage(prByDay).orElse("Кажется эту неделю ты не работал");
    }

    private Optional<String> generateMessage(@NonNull Map<LocalDate, List<PullRequest>> prByDay) {
        if (!prByDay.isEmpty()) {
            final StringBuilder message = new StringBuilder("Твой отчет на эту неделю:").append(Smile.TWO_BR);
            for (Map.Entry<LocalDate, List<PullRequest>> entry : prByDay.entrySet()) {
                message.append(dayOfWeek(entry.getKey())).append(": ");
                for (PullRequest pullRequest : entry.getValue()) {
                    message.append(" - ").append(pullRequest.getName()).append(Smile.BR)
                            .append("  --").append(pullRequest.getDescription());
                }
            }
        }
        return Optional.empty();
    }

    private String dayOfWeek(LocalDate date) {
        switch (date.getDayOfWeek()) {
            case MONDAY:
                return "Понедельник";
            case SUNDAY:
                return "Воскресенье";
            case TUESDAY:
                return "Вторник";
            case SATURDAY:
                return "Суббота";
            case THURSDAY:
                return "Четверг";
            case WEDNESDAY:
                return "Среда";
            case FRIDAY:
                return "Пятница";
        }
        return "";
    }

}
