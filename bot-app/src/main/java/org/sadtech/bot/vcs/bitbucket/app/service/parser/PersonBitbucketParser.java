package org.sadtech.bot.vcs.bitbucket.app.service.parser;

import lombok.RequiredArgsConstructor;
import org.sadtech.bot.vcs.bitbucket.sdk.domain.UserJson;
import org.sadtech.bot.vcs.bitbucket.sdk.domain.sheet.UserSheetJson;
import org.sadtech.bot.vcs.core.config.properties.BitbucketProperty;
import org.sadtech.bot.vcs.core.utils.Utils;
import org.sadtech.bot.vsc.bitbucketbot.context.domain.entity.Person;
import org.sadtech.bot.vsc.bitbucketbot.context.service.PersonService;
import org.sadtech.bot.vsc.bitbucketbot.context.service.parser.PersonParser;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonBitbucketParser implements PersonParser {

    private final PersonService personService;
    private final ConversionService conversionService;

    private final BitbucketProperty bitbucketProperty;

    @Override
    public void scanNewPerson() {
        Optional<UserSheetJson> sheetJson = Utils.urlToJson(bitbucketProperty.getUrlUsers(), bitbucketProperty.getToken(), UserSheetJson.class);
        while (sheetJson.isPresent() && sheetJson.get().hasContent()) {
            final UserSheetJson sheetUsers = sheetJson.get();
            final List<UserJson> users = sheetUsers.getValues();
            final Set<String> logins = users.stream().map(UserJson::getName).collect(Collectors.toSet());
            final Set<String> existsLogins = personService.existsByLogin(logins);
            final Set<Person> newUsers = users.stream()
                    .filter(userJson -> !existsLogins.contains(userJson.getName()))
                    .map(userJson -> conversionService.convert(userJson, Person.class))
                    .collect(Collectors.toSet());
            if (!newUsers.isEmpty()) {
                personService.createAll(newUsers);
            }
            if (sheetUsers.getNextPageStart() != null) {
                sheetJson = Utils.urlToJson(bitbucketProperty.getUrlUsers() + sheetUsers.getNextPageStart(), bitbucketProperty.getToken(), UserSheetJson.class);
            } else {
                break;
            }
        }
    }

}
