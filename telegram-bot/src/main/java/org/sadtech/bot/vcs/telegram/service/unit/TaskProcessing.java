package org.sadtech.bot.vcs.telegram.service.unit;

import lombok.RequiredArgsConstructor;
import org.sadtech.bot.vsc.bitbucketbot.context.domain.TaskStatus;
import org.sadtech.bot.vsc.bitbucketbot.context.domain.entity.Person;
import org.sadtech.bot.vsc.bitbucketbot.context.domain.entity.Task;
import org.sadtech.bot.vsc.bitbucketbot.context.exception.NotFoundException;
import org.sadtech.bot.vsc.bitbucketbot.context.service.PersonService;
import org.sadtech.bot.vsc.bitbucketbot.context.service.TaskService;
import org.sadtech.social.bot.service.usercode.ProcessingData;
import org.sadtech.social.core.domain.BoxAnswer;
import org.sadtech.social.core.domain.content.Message;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

/**
 * // TODO: 17.09.2020 Добавить описание.
 *
 * @author upagge 17.09.2020
 */
@Component
@RequiredArgsConstructor
public class TaskProcessing implements ProcessingData<Message> {

    private final PersonService personService;
    private final TaskService taskService;

    @Override
    public BoxAnswer processing(Message message) {
        final Person person = personService.getByTelegramId(message.getPersonId())
                .orElseThrow(() -> new NotFoundException("Ошибочка"));
        final List<Task> tasks = taskService.getAllByResponsibleAndStatus(person.getLogin(), TaskStatus.OPEN);
        String messageText;
        if (tasks.isEmpty()) {
            messageText = "Задач нет";
        } else {
            final String tasksString = tasks.stream()
                    .map(this::createTaskString)
                    .collect(Collectors.joining("\n"));
            messageText = MessageFormat.format(
                    "Список ваших задач:\n\n{0}",
                    tasksString
            );
        }
        return BoxAnswer.of(messageText);
    }

    private String createTaskString(Task task) {
        return MessageFormat.format(
                "- [{0}]({1})",
                task.getDescription(), task.getUrl()
        );
    }

}
