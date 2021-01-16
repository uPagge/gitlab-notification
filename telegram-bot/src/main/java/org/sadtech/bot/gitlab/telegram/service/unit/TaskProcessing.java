package org.sadtech.bot.gitlab.telegram.service.unit;

import lombok.RequiredArgsConstructor;
import org.sadtech.bot.gitlab.context.domain.entity.Task;
import org.sadtech.bot.gitlab.context.service.TaskService;
import org.sadtech.social.core.domain.BoxAnswer;
import org.sadtech.social.core.domain.ProcessingData;
import org.sadtech.social.core.domain.content.Message;

/**
 * // TODO: 17.09.2020 Добавить описание.
 *
 * @author upagge 17.09.2020
 */
//@Component
@RequiredArgsConstructor
public class TaskProcessing implements ProcessingData<Message> {

    private final TaskService taskService;

    @Override
    public BoxAnswer processing(Message message) {
//        final Person person = personService.getByTelegramId(message.getPersonId())
//                .orElseThrow(() -> new NotFoundException("Ошибочка"));
//        final List<Task> tasks = taskService.getAllByResponsibleAndStatus(person.getLogin(), TaskStatus.OPEN);
//        String messageText;
//        if (tasks.isEmpty()) {
//            messageText = "Задач нет";
//        } else {
//            final String tasksString = tasks.stream()
//                    .map(this::createTaskString)
//                    .collect(Collectors.joining("\n"));
//            messageText = MessageFormat.format(
//                    "Список ваших задач:\n\n{0}",
//                    tasksString
//            );
//        }
//        return BoxAnswer.of(messageText);
        return null;
    }

    private String createTaskString(Task task) {
//        return MessageFormat.format(
//                "- [{0}]({1})",
//                task.getDescription(), task.getUrl()
//        );
        return null;
    }

}
