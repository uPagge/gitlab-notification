package org.sadtech.bot.vcs.bitbucket.app.service.parser;

import lombok.extern.slf4j.Slf4j;
import org.sadtech.bot.gitlab.context.domain.entity.Person;
import org.sadtech.bot.gitlab.context.service.PersonService;
import org.sadtech.bot.gitlab.context.service.PullRequestsService;
import org.sadtech.bot.gitlab.core.config.properties.BitbucketProperty;
import org.sadtech.bot.vcs.bitbucket.core.AbstractPullRequestBitbucketParser;
import org.sadtech.bot.vsc.context.service.PullRequestParser;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class PullRequestBitbucketParser extends AbstractPullRequestBitbucketParser implements PullRequestParser {

    private final PersonService personService;
    private final BitbucketProperty bitbucketProperty;

    protected PullRequestBitbucketParser(
            PullRequestsService pullRequestsService,
            PersonService personService,
            ConversionService conversionService,
            BitbucketProperty bitbucketProperty
    ) {
        super(pullRequestsService, conversionService);
        this.personService = personService;
        this.bitbucketProperty = bitbucketProperty;
    }

    @Override
    public void parsingOldPullRequest() {
        processingOldPullRequests(bitbucketProperty.getUrlPullRequestOpen(), bitbucketProperty.getUrlPullRequestClose());
    }

    @Override
    public void parsingNewPullRequest() {
        final List<Person> users = personService.getAllRegister();
        for (Person user : users) {
            createNewPullRequest(bitbucketProperty.getUrlPullRequestOpen(), user.getToken());
        }
    }

    @Override
    protected Set<Long> getExistsPullRequestIds(String bitbucketUrl) {
        final List<Person> persons = personService.getAllRegister();
        final Set<Long> ids = new HashSet<>();
        for (Person person : persons) {
            ids.addAll(updateOldPullRequests(bitbucketUrl, person.getToken()));
        }
        return ids;
    }

}
