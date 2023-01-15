package dev.struchkov.bot.gitlab.telegram.service;

import dev.struchkov.bot.gitlab.context.domain.PersonInformation;
import dev.struchkov.bot.gitlab.context.domain.notify.Notify;
import dev.struchkov.bot.gitlab.context.service.MessageSendService;
import dev.struchkov.bot.gitlab.telegram.service.notify.NotifyBoxAnswerGenerator;
import dev.struchkov.godfather.main.domain.BoxAnswer;
import dev.struchkov.godfather.telegram.simple.context.service.TelegramSending;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Отправляет сообщение в телеграмм.
 *
 * @author upagge 17.09.2020
 */
@Service
public class MessageSendTelegramService implements MessageSendService {

    private final Map<String, NotifyBoxAnswerGenerator> generatorMap;
    private final TelegramSending sending;

    private final PersonInformation personInformation;

    public MessageSendTelegramService(
            List<NotifyBoxAnswerGenerator> generators,
            TelegramSending sending,
            PersonInformation personInformation
    ) {
        this.generatorMap = generators.stream().collect(Collectors.toMap(NotifyBoxAnswerGenerator::getNotifyType, n -> n));
        this.sending = sending;
        this.personInformation = personInformation;
    }

    @Override
    public void send(@NonNull Notify notify) {
        getGenerator(notify.getType())
                .map(generator -> {
                    final BoxAnswer answer = generator.generate(notify);
                    answer.setRecipientIfNull(personInformation.getTelegramId());
                    return answer;
                })
                .ifPresent(sending::send);
    }

    private Optional<NotifyBoxAnswerGenerator> getGenerator(String notifyType) {
        return Optional.ofNullable(generatorMap.get(notifyType));
    }

}
