package dev.struchkov.bot.gitlab.telegram.service;

import dev.struchkov.bot.gitlab.core.config.properties.GitlabProperty;
import dev.struchkov.godfather.simple.domain.BoxAnswer;
import dev.struchkov.godfather.simple.domain.action.PreSendProcessing;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static dev.struchkov.haiti.utils.Checker.checkNotNull;

/**
 * Заменяет урл, который назначает гитлаб на любой другой перед отправкой сообщения. Потому что иногда у self-host гитлабов урлы, которые не преобразуются телеграммом в ссылки. В этом случае имеет смысл менять их на ip.
 *
 * @author upagge 18.09.2020
 */
@Component
@RequiredArgsConstructor
public class ReplaceUrlLocalhost implements PreSendProcessing {

    private final GitlabProperty property;

    @Override
    public BoxAnswer pretreatment(BoxAnswer boxAnswer) {
        if (checkNotNull(property.getReplaceUrl()) && !"${GITLAB_REPLACE_URL}".equals(property.getReplaceUrl())) {
            boxAnswer.setMessage(boxAnswer.getMessage().replace(property.getReplaceUrl(), property.getBaseUrl()));
        }
        return boxAnswer;
    }

}
