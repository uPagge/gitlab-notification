package dev.struchkov.bot.gitlab.telegram.service;

import dev.struchkov.bot.gitlab.core.config.properties.GitlabProperty;
import dev.struchkov.godfather.telegram.service.SendPreProcessing;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Заменяет урл, который назначает гитлаб на любой другой перед отправкой сообщения. Потому что иногда у self-host гитлабов урлы, которые не преобразуются телеграммом в ссылки. В этом случае имеет смысл менять их на ip.
 *
 * @author upagge 18.09.2020
 */
@Component
@RequiredArgsConstructor
public class ReplaceUrlLocalhost implements SendPreProcessing {

    private final GitlabProperty property;

    @Override
    public String pretreatment(String s) {
        if (property.getReplaceUrl() != null && !"${GITLAB_REPLACE_URL}".equals(property.getReplaceUrl())) {
            return s.replace(property.getReplaceUrl(), property.getBaseUrl());
        }
        return s;
    }

}
