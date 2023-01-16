package dev.struchkov.bot.gitlab.telegram.unit.flow;

import dev.struchkov.bot.gitlab.context.domain.PersonInformation;
import dev.struchkov.bot.gitlab.context.service.AppSettingService;
import dev.struchkov.bot.gitlab.context.service.DiscussionService;
import dev.struchkov.bot.gitlab.context.service.MergeRequestsService;
import dev.struchkov.bot.gitlab.context.service.PipelineService;
import dev.struchkov.bot.gitlab.context.service.ProjectService;
import dev.struchkov.bot.gitlab.core.service.parser.DiscussionParser;
import dev.struchkov.bot.gitlab.core.service.parser.MergeRequestParser;
import dev.struchkov.bot.gitlab.core.service.parser.PipelineParser;
import dev.struchkov.bot.gitlab.core.service.parser.ProjectParser;
import dev.struchkov.bot.gitlab.telegram.utils.Keys;
import dev.struchkov.godfather.main.domain.annotation.Unit;
import dev.struchkov.godfather.main.domain.content.Mail;
import dev.struchkov.godfather.simple.core.unit.AnswerText;
import dev.struchkov.godfather.simple.core.unit.MainUnit;
import dev.struchkov.godfather.simple.data.StorylineContext;
import dev.struchkov.godfather.telegram.domain.attachment.ButtonClickAttachment;
import dev.struchkov.godfather.telegram.main.core.util.Attachments;
import dev.struchkov.godfather.telegram.simple.context.service.TelegramSending;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static dev.struchkov.bot.gitlab.telegram.utils.UnitName.CHECK_PARSER_PRIVATE_PROJECT_NO;
import static dev.struchkov.bot.gitlab.telegram.utils.UnitName.CHECK_PARSER_PRIVATE_PROJECT_YES;
import static dev.struchkov.bot.gitlab.telegram.utils.UnitName.CHECK_PARSE_OWNER_PROJECT_NO;
import static dev.struchkov.bot.gitlab.telegram.utils.UnitName.CHECK_PARSE_OWNER_PROJECT_YES;
import static dev.struchkov.bot.gitlab.telegram.utils.UnitName.END_SETTING;
import static dev.struchkov.bot.gitlab.telegram.utils.UnitName.FIRST_START;
import static dev.struchkov.bot.gitlab.telegram.utils.UnitName.TEXT_PARSER_PRIVATE_PROJECT;
import static dev.struchkov.bot.gitlab.telegram.utils.UnitName.TEXT_PARSE_OWNER_PROJECT;
import static dev.struchkov.godfather.main.core.unit.UnitActiveType.AFTER;
import static dev.struchkov.godfather.main.domain.BoxAnswer.boxAnswer;
import static dev.struchkov.godfather.main.domain.BoxAnswer.replaceBoxAnswer;
import static dev.struchkov.godfather.main.domain.keyboard.button.SimpleButton.simpleButton;
import static dev.struchkov.godfather.main.domain.keyboard.simple.SimpleKeyBoardLine.simpleLine;
import static dev.struchkov.godfather.telegram.domain.keyboard.InlineKeyBoard.inlineKeyBoard;
import static dev.struchkov.godfather.telegram.simple.core.util.TriggerChecks.clickButtonRaw;
import static java.text.MessageFormat.format;

/**
 * TODO: Добавить описание класса.
 *
 * @author upagge [30.01.2020]
 */
@Component
@RequiredArgsConstructor
public class InitSettingFlow {

    private final TelegramSending sending;
    private final StorylineContext context;

    private final PersonInformation personInformation;

    private final AppSettingService settingService;

    private final ProjectParser projectParser;
    private final MergeRequestParser mergeRequestParser;
    private final PipelineParser pipelineParser;
    private final DiscussionParser discussionParser;

    private final ProjectService projectService;
    private final MergeRequestsService mergeRequestsService;
    private final PipelineService pipelineService;
    private final DiscussionService discussionService;

    @Unit(value = FIRST_START, main = true)
    public AnswerText<Mail> firstStart(
            @Unit(value = TEXT_PARSER_PRIVATE_PROJECT) MainUnit<Mail> textParserPrivateProject
    ) {
        return AnswerText.<Mail>builder()
                .triggerCheck(mail -> {
                    final boolean isAccess = personInformation.getTelegramId().equals(mail.getPersonId());
                    if (isAccess) {
                        return settingService.isFirstStart();
                    }
                    return false;
                })
                .answer(
                        boxAnswer(
                                """
                                        Hello!
                                        This bot will help you keep your finger on the pulse of all your GitLab projects.
                                        Press start to start initial setup 👇
                                        """,
                                inlineKeyBoard(simpleButton("start", TEXT_PARSER_PRIVATE_PROJECT))
                        )
                )
                .next(textParserPrivateProject)
                .build();
    }

    @Unit(value = TEXT_PARSER_PRIVATE_PROJECT)
    public AnswerText<Mail> textParserPrivateProject(
            @Unit(CHECK_PARSER_PRIVATE_PROJECT_YES) MainUnit<Mail> checkParserPrivateProject
    ) {
        return AnswerText.<Mail>builder()
                .answer(() -> replaceBoxAnswer(
                                "Start tracking private projects?",
                                inlineKeyBoard(
                                        simpleLine(
                                                simpleButton("Yes", "YES"),
                                                simpleButton("No", "NO")
                                        )
                                )
                        )
                )
                .next(checkParserPrivateProject)
                .build();
    }

    @Unit(CHECK_PARSER_PRIVATE_PROJECT_YES)
    public AnswerText<Mail> checkParserPrivateProjectYes(
            @Unit(TEXT_PARSE_OWNER_PROJECT) MainUnit<Mail> textParseOwnerProject
    ) {
        final String step1 = """
                -- -- -- -- --
                🔘 Scanning of private projects has begun.
                ⌛ Wait...
                -- -- -- -- --
                """;

        final String step2 = """
                -- -- -- -- --
                🟢 Projects have been successfully added to tracking. Found: {0}
                🔘 Scanning merge requests in found projects.
                ⌛ Wait...
                -- -- -- -- --
                """;
        final String step3 = """
                -- -- -- -- --
                🟢 Projects have been successfully added to tracking. Found: {0}
                🟢 Merge requests have been successfully added. Found: {1}
                🔘 Scanning pipelines in found merge requests.
                ⌛ Wait...
                -- -- -- -- --
                """;

        final String step4 = """
                -- -- -- -- --
                🟢 Projects have been successfully added to tracking. Found: {0}
                🟢 Merge requests have been successfully added. Found: {1}
                🟢 Pipelines have been successfully added. Found: {2}
                🔘 Scanning threads in merge requests.
                ⌛ Wait...
                -- -- -- -- --
                """;

        final String finalAnswer = """
                -- -- -- -- --
                🟢 Projects have been successfully added to tracking. Found: {0}
                🟢 Merge requests have been successfully added. Found: {1}
                🟢 Pipelines have been successfully added. Found: {2}
                🟢 Threads have been successfully added. Found: {3}
                -- -- -- -- --
                """;

        return AnswerText.<Mail>builder()
                .triggerCheck(clickButtonRaw("YES"))
                .answer(mail -> {
                    final String personId = mail.getPersonId();
                    final Integer messageId = Attachments.findFirstButtonClick(mail.getAttachments())
                            .map(ButtonClickAttachment::getMessageId)
                            .orElseThrow();
                    sending.replaceMessage(personId, messageId, boxAnswer(step1));

                    projectParser.parseAllPrivateProject();
                    final int projectCount = projectService.getAllIds().size();
                    sending.replaceMessage(personId, messageId, boxAnswer(format(step2, projectCount)));

                    mergeRequestParser.parsingNewMergeRequest();
                    final int mrCount = mergeRequestsService.getAllIds().size();
                    sending.replaceMessage(personId, messageId, boxAnswer(format(step3, projectCount, mrCount)));

                    pipelineParser.scanNewPipeline();
                    final int pipelineCount = pipelineService.getAllIds().size();
                    sending.replaceMessage(personId, messageId, boxAnswer(format(step4, projectCount, mrCount, pipelineCount)));

                    discussionParser.scanNewDiscussion();
                    final int discussionCount = discussionService.getAllIds().size();

                    context.save(mail.getPersonId(), Keys.INIT_SETTING_PRIVATE_PROJECT_MESSAGE_ID, messageId);
                    return replaceBoxAnswer(format(finalAnswer, pipelineCount, mrCount, pipelineCount, discussionCount));
                })
                .next(textParseOwnerProject)
                .build();
    }

    @Unit(CHECK_PARSER_PRIVATE_PROJECT_NO)
    public AnswerText<Mail> checkParserPrivateProjectNo(
            @Unit(TEXT_PARSE_OWNER_PROJECT) MainUnit<Mail> textParseOwnerProject
    ) {
        return AnswerText.<Mail>builder()
                .triggerPhrase("NO")
                .answer(mail -> {
                    final Integer messageId = Attachments.findFirstButtonClick(mail.getAttachments())
                            .map(ButtonClickAttachment::getMessageId)
                            .orElseThrow();

                    context.save(mail.getPersonId(), Keys.INIT_SETTING_PRIVATE_PROJECT_MESSAGE_ID, messageId);
                    replaceBoxAnswer("Okay, I won't scan private projects.");
                })
                .next(textParseOwnerProject)
                .build();
    }

    @Unit(TEXT_PARSE_OWNER_PROJECT)
    public AnswerText<Mail> textParseOwnerProject(
            @Unit(CHECK_PARSE_OWNER_PROJECT_YES) MainUnit<Mail> checkParseOwnerProjectYes,
            @Unit(CHECK_PARSE_OWNER_PROJECT_NO) MainUnit<Mail> checkParseOwnerProjectNo
    ) {
        return AnswerText.<Mail>builder()
                .answer(
                        boxAnswer(
                                "Start tracking public projects that you own?",
                                inlineKeyBoard(
                                        simpleLine(
                                                simpleButton("Yes", "YES"),
                                                simpleButton("No", "NO")
                                        )
                                )
                        )
                )
                .activeType(AFTER)
                .next(checkParseOwnerProjectYes)
                .next(checkParseOwnerProjectNo)
                .build();
    }

    @Unit(CHECK_PARSE_OWNER_PROJECT_YES)
    public AnswerText<Mail> checkParseOwnerProjectYes(
            @Unit(END_SETTING) MainUnit<Mail> endSetting
    ) {
        final String step1 = """
                -- -- -- -- --
                🔘 Scanning of public projects has begun.
                ⌛ Wait...
                -- -- -- -- --
                """;

        final String step2 = """
                -- -- -- -- --
                🟢 Projects have been successfully added to tracking. Found: {0}
                🔘 Scanning merge requests in found projects.
                ⌛ Wait...
                -- -- -- -- --
                """;
        final String step3 = """
                -- -- -- -- --
                🟢 Projects have been successfully added to tracking. Found: {0}
                🟢 Merge requests have been successfully added. Found: {1}
                🔘 Scanning pipelines in found merge requests.
                ⌛ Wait...
                -- -- -- -- --
                """;

        final String step4 = """
                -- -- -- -- --
                🟢 Projects have been successfully added to tracking. Found: {0}
                🟢 Merge requests have been successfully added. Found: {1}
                🟢 Pipelines have been successfully added. Found: {2}
                🔘 Scanning threads in merge requests.
                ⌛ Wait...
                -- -- -- -- --
                """;

        final String finalAnswer = """
                -- -- -- -- --
                🟢 Projects have been successfully added to tracking. Found: {0}
                🟢 Merge requests have been successfully added. Found: {1}
                🟢 Pipelines have been successfully added. Found: {2}
                🟢 Threads have been successfully added. Found: {3}
                -- -- -- -- --
                """;

        return AnswerText.<Mail>builder()
                .triggerCheck(clickButtonRaw("YES"))
                .answer(mail -> {
                    final String personId = mail.getPersonId();
                    final Integer messageId = Attachments.findFirstButtonClick(mail.getAttachments())
                            .map(ButtonClickAttachment::getMessageId)
                            .orElseThrow();
                    sending.replaceMessage(personId, messageId, boxAnswer(step1));

                    projectParser.parseAllProjectOwner();
                    final int projectCount = projectService.getAllIds().size();
                    sending.replaceMessage(personId, messageId, boxAnswer(format(step2, projectCount)));

                    mergeRequestParser.parsingNewMergeRequest();
                    final int mrCount = mergeRequestsService.getAllIds().size();
                    sending.replaceMessage(personId, messageId, boxAnswer(format(step3, projectCount, mrCount)));

                    pipelineParser.scanNewPipeline();
                    final int pipelineCount = pipelineService.getAllIds().size();
                    sending.replaceMessage(personId, messageId, boxAnswer(format(step4, projectCount, mrCount, pipelineCount)));

                    discussionParser.scanNewDiscussion();
                    final int discussionCount = discussionService.getAllIds().size();

                    context.save(mail.getPersonId(), Keys.INIT_SETTING_PUBLIC_PROJECT_MESSAGE_ID, messageId);
                    return replaceBoxAnswer(format(finalAnswer, pipelineCount, mrCount, pipelineCount, discussionCount));
                })
                .next(endSetting)
                .build();
    }

    @Unit(CHECK_PARSE_OWNER_PROJECT_NO)
    public AnswerText<Mail> checkParseOwnerProjectNo(
            @Unit(END_SETTING) MainUnit<Mail> endSetting
    ) {
        return AnswerText.<Mail>builder()
                .triggerCheck(clickButtonRaw("NO"))
                .answer(mail -> {
                    final Integer messageId = Attachments.findFirstButtonClick(mail.getAttachments())
                            .map(ButtonClickAttachment::getMessageId)
                            .orElseThrow();
                    context.save(mail.getPersonId(), Keys.INIT_SETTING_PUBLIC_PROJECT_MESSAGE_ID, messageId);
                    return replaceBoxAnswer("Okay, I won't scan public projects.");
                })
                .next(endSetting)
                .build();
    }

    @Unit(END_SETTING)
    public AnswerText<Mail> endSetting() {
        return AnswerText.<Mail>builder()
                .activeType(AFTER)
                .answer(
                        mail -> {
                            context.save(mail.getPersonId(), Keys.INIT_SETTING_FINISH, Boolean.TRUE);
                            settingService.turnOnAllNotify();
                            settingService.disableFirstStart();
                            return boxAnswer("""
                                            Configuration completed successfully
                                            Developer: [uPagge](https://mark.struchkov.dev)
                                            """,
                                    inlineKeyBoard(simpleButton("open menu", "INIT_SETTING_OPEN_MENU"))
                            );
                        }
                )
                .build();
    }

}
