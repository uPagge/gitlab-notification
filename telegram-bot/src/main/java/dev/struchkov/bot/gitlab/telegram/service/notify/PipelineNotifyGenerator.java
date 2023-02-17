package dev.struchkov.bot.gitlab.telegram.service.notify;

import dev.struchkov.bot.gitlab.context.domain.notify.pipeline.PipelineNotify;
import dev.struchkov.bot.gitlab.context.service.ProjectService;
import dev.struchkov.bot.gitlab.context.utils.Icons;
import dev.struchkov.godfather.simple.domain.BoxAnswer;
import dev.struchkov.haiti.utils.Strings;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static dev.struchkov.bot.gitlab.telegram.utils.UnitName.DELETE_MESSAGE;
import static dev.struchkov.godfather.main.domain.keyboard.button.SimpleButton.simpleButton;
import static dev.struchkov.godfather.main.domain.keyboard.simple.SimpleKeyBoardLine.simpleLine;
import static dev.struchkov.godfather.simple.domain.BoxAnswer.boxAnswer;
import static dev.struchkov.godfather.telegram.domain.keyboard.InlineKeyBoard.inlineKeyBoard;
import static dev.struchkov.godfather.telegram.domain.keyboard.button.UrlButton.urlButton;

@Service
@RequiredArgsConstructor
public class PipelineNotifyGenerator implements NotifyBoxAnswerGenerator<PipelineNotify> {

    private final ProjectService projectService;

    @Override
    public BoxAnswer generate(PipelineNotify notify) {
        final StringBuilder builder = new StringBuilder(Icons.BUILD).append(" *New pipeline | ").append(notify.getPipelineId()).append("*");

        builder
                .append(Icons.HR)
                .append(notify.getOldStatus().getIcon()).append(" ").append(notify.getOldStatus()).append(Icons.ARROW).append(notify.getNewStatus().getIcon()).append(" ").append(notify.getNewStatus())
                .append(Icons.HR);

        final Optional<String> optProjectName = projectService.getProjectNameById(notify.getProjectId())
                .map(Strings::escapeMarkdown);
        if (optProjectName.isPresent()) {
            final String projectName = optProjectName.get();
            builder.append(Icons.PROJECT).append(": ").append(projectName).append("\n");
        }

        builder
                .append(Icons.TREE).append(": ").append(notify.getRefName());

        return boxAnswer(
                builder.toString(),
                inlineKeyBoard(
                        simpleLine(
                                simpleButton(Icons.VIEW, DELETE_MESSAGE),
                                urlButton(Icons.LINK, notify.getWebUrl())
                        )
                )
        );
    }

    @Override
    public String getNotifyType() {
        return PipelineNotify.TYPE;
    }

}
