package com.tsc.bitbucketbot.scheduler;

import com.tsc.bitbucketbot.config.BitbucketConfig;
import com.tsc.bitbucketbot.domain.entity.User;
import com.tsc.bitbucketbot.dto.bitbucket.UserJson;
import com.tsc.bitbucketbot.dto.bitbucket.sheet.UserSheetJson;
import com.tsc.bitbucketbot.service.UserService;
import com.tsc.bitbucketbot.service.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * TODO: Добавить описание класса.
 *
 * @author upagge [02.02.2020]
 */
@Service
@RequiredArgsConstructor
public class SchedulerNewUser {

    private static final String URL = "http://192.168.236.164:7990/rest/api/1.0/admin/users";
    private final UserService userService;
    private final ConversionService conversionService;
    private final BitbucketConfig bitbucketConfig;

    @Scheduled(fixedRate = 86400000)
    private void scan() {
        Optional<UserSheetJson> sheetJson = Utils.urlToJson(URL, bitbucketConfig.getToken(), UserSheetJson.class);
        while (sheetJson.isPresent() && sheetJson.get().getValues()!=null && !sheetJson.get().getValues().isEmpty()) {
            final UserSheetJson sheetUsers = sheetJson.get();
            final List<UserJson> users = sheetUsers.getValues();
            final Set<String> logins = users.stream().map(UserJson::getName).collect(Collectors.toSet());
            final Set<String> existsLogins = userService.existsByLogin(logins);
            final Set<User> newUsers = users.stream()
                    .filter(userJson -> !existsLogins.contains(userJson.getName()))
                    .map(userJson -> conversionService.convert(userJson, User.class))
                    .collect(Collectors.toSet());
            if (!newUsers.isEmpty()) {
                userService.addAll(newUsers);
            }
            if (sheetUsers.getNextPageStart() != null) {
                sheetJson = Utils.urlToJson(URL + sheetUsers.getNextPageStart(), bitbucketConfig.getToken(), UserSheetJson.class);
            } else {
                break;
            }
        }
    }

}
