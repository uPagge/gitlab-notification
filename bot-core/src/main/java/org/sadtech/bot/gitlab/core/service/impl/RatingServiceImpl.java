package org.sadtech.bot.gitlab.core.service.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.sadtech.bot.gitlab.context.domain.PointType;
import org.sadtech.bot.gitlab.context.domain.entity.Person;
import org.sadtech.bot.gitlab.context.domain.entity.RatingHistory;
import org.sadtech.bot.gitlab.context.domain.entity.RatingList;
import org.sadtech.bot.gitlab.context.exception.NotFoundException;
import org.sadtech.bot.gitlab.context.repository.RatingHistoryRepository;
import org.sadtech.bot.gitlab.context.repository.RatingListRepository;
import org.sadtech.bot.gitlab.context.service.PersonService;
import org.sadtech.bot.gitlab.context.service.RatingService;
import org.sadtech.bot.gitlab.context.utils.Smile;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * // TODO: 01.10.2020 Добавить описание.
 *
 * @author upagge 01.10.2020
 */
@Service
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {

    private final RatingHistoryRepository ratingHistoryRepository;
    private final RatingListRepository ratingListRepository;
    private final PersonService personService;

    @Override
    public void addRating(@NonNull String login, @NonNull PointType type, @NonNull Integer points) {
        final RatingHistory ratingHistory = new RatingHistory();
        ratingHistory.setLogin(login);
        ratingHistory.setPoints(points);
        ratingHistory.setType(type);
        ratingHistory.setDateAdd(LocalDateTime.now());
        ratingHistoryRepository.save(ratingHistory);
    }

    @Override
    public void ratingRecalculation() {
        AtomicInteger i = new AtomicInteger();
        final List<RatingList> newRatingList = ratingHistoryRepository.findAllByDateAddBetween(LocalDateTime.now().minusDays(30L), LocalDateTime.now()).stream()
                .collect(Collectors.groupingBy(RatingHistory::getLogin, Collectors.summingInt(RatingHistory::getPoints)))
                .entrySet().stream()
                .map(this::createRatingList)
                .collect(Collectors.toList());
        final Set<String> ratingListLogins = newRatingList.stream()
                .map(RatingList::getLogin)
                .collect(Collectors.toSet());
        final Set<String> regLogins = personService.getAllRegister().stream()
                .map(Person::getLogin)
                .collect(Collectors.toSet());
        newRatingList.addAll(
                regLogins.stream()
                        .filter(s -> !ratingListLogins.contains(s))
                        .map(this::createEmptyRatingList)
                        .collect(Collectors.toList())
        );
        ratingListRepository.saveAll(
                newRatingList.stream()
                        .sorted()
                        .peek(ratingList -> ratingList.setNumber(i.getAndIncrement()))
                        .collect(Collectors.toList())
        );
    }

    private RatingList createEmptyRatingList(String s) {
        final RatingList ratingList = new RatingList();
        ratingList.setLogin(s);
        ratingList.setPoints(0);
        return ratingList;
    }

    private RatingList createRatingList(Map.Entry<String, Integer> entry) {
        final RatingList ratingList = new RatingList();
        ratingList.setLogin(entry.getKey());
        ratingList.setPoints(entry.getValue());
        return ratingList;
    }

    @Override
    public String getRatingTop(@NonNull String login) {
        final RatingList personRating = ratingListRepository.getByLogin(login)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        final long countPerson = ratingListRepository.count();
        final String threeMessage = ratingListRepository.findFirstThree().stream()
                .map(this::createString)
                .collect(Collectors.joining("\n"));
        final String lastMessage = ratingListRepository.findLastThree().stream()
                .limit(countPerson - 3 < 0 ? 0 : countPerson - 3)
                .map(this::createString)
                .collect(Collectors.joining("\n"));

        String message;

        if (personRating.getPoints() == 0) {
            message = Smile.SADLY + " У вас не обнаружена активность в битбакете, поэтому вы не учавствуйте в рейтинге" +
                    "\n\n" + Smile.TOP + " Рейтинговая таблица " + Smile.TOP + Smile.HR + threeMessage + "\n... ... ... ... ...\n";
        } else {
            message = Smile.TOP + " Рейтинговая таблица " + Smile.TOP + Smile.HR + threeMessage;
            final Integer numberRatingList = personRating.getNumber();
            if (numberRatingList <= 2) {
                if (countPerson > 3) {
                    message += "\n... ... ... ... ...\n";
                }
            } else if (numberRatingList > 3 && numberRatingList <= (countPerson - 3)) {
                message += "\n... ... ... ... ...\n" + personRating.getNumber() + ": " + personRating.getLogin() + "\n... ... ... ... ...\n";
            } else {
                message += "\n... ... ... ... ...\n";
            }
        }
        message += lastMessage;
        return message;
    }

    private String createString(RatingList ratingList) {
        String message = "";
        final Integer number = ratingList.getNumber();
        if (number == 0) {
            message += Smile.TOP_ONE.getValue() + " " + ratingList.getLogin() + " " + Smile.TOP_ONE.getValue();
        } else if (number == 1) {
            message += Smile.TOP_TWO.getValue() + " " + ratingList.getLogin() + " " + Smile.TOP_TWO.getValue();
        } else if (number == 2) {
            message += Smile.TOP_THREE.getValue() + " " + ratingList.getLogin() + " " + Smile.TOP_THREE.getValue();
        } else {
            message += Smile.KAKASHKA.getValue() + " " + ratingList.getLogin();
        }
        return message;
    }

}
