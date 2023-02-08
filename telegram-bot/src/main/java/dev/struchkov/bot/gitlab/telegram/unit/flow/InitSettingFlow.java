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
import dev.struchkov.godfather.main.domain.annotation.Unit;
import dev.struchkov.godfather.main.domain.content.Mail;
import dev.struchkov.godfather.simple.core.unit.AnswerText;
import dev.struchkov.godfather.simple.core.unit.MainUnit;
import dev.struchkov.godfather.telegram.domain.attachment.ButtonClickAttachment;
import dev.struchkov.godfather.telegram.main.core.util.Attachments;
import dev.struchkov.godfather.telegram.simple.context.service.TelegramSending;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static dev.struchkov.bot.gitlab.telegram.utils.UnitName.AUTO_PARSE_OWNER_PROJECT;
import static dev.struchkov.bot.gitlab.telegram.utils.UnitName.AUTO_PARSE_PRIVATE_PROJECT;
import static dev.struchkov.bot.gitlab.telegram.utils.UnitName.CHECK_PARSER_PRIVATE_PROJECT_NO;
import static dev.struchkov.bot.gitlab.telegram.utils.UnitName.CHECK_PARSER_PRIVATE_PROJECT_YES;
import static dev.struchkov.bot.gitlab.telegram.utils.UnitName.CHECK_PARSE_OWNER_PROJECT_NO;
import static dev.struchkov.bot.gitlab.telegram.utils.UnitName.CHECK_PARSE_OWNER_PROJECT_YES;
import static dev.struchkov.bot.gitlab.telegram.utils.UnitName.END_SETTING;
import static dev.struchkov.bot.gitlab.telegram.utils.UnitName.FIRST_START;
import static dev.struchkov.bot.gitlab.telegram.utils.UnitName.TEXT_AUTO_PARSE_OWNER_PROJECT;
import static dev.struchkov.bot.gitlab.telegram.utils.UnitName.TEXT_AUTO_PARSE_PRIVATE_PROJECT;
import static dev.struchkov.bot.gitlab.telegram.utils.UnitName.TEXT_PARSER_OWNER_PROJECT;
import static dev.struchkov.bot.gitlab.telegram.utils.UnitName.TEXT_PARSER_PRIVATE_PROJECT;
import static dev.struchkov.godfather.main.core.unit.UnitActiveType.AFTER;
import static dev.struchkov.godfather.main.domain.BoxAnswer.boxAnswer;
import static dev.struchkov.godfather.main.domain.BoxAnswer.replaceBoxAnswer;
import static dev.struchkov.godfather.main.domain.keyboard.button.SimpleButton.simpleButton;
import static dev.struchkov.godfather.main.domain.keyboard.simple.SimpleKeyBoardLine.simpleLine;
import static dev.struchkov.godfather.telegram.domain.keyboard.InlineKeyBoard.inlineKeyBoard;
import static dev.struchkov.godfather.telegram.simple.core.util.TriggerChecks.clickButtonRaw;
import static dev.struchkov.godfather.telegram.simple.core.util.TriggerChecks.isClickButton;
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

    private final ScheduledExecutorService scheduledExecutorService;

    @Unit(value = FIRST_START, main = true)
    public AnswerText<Mail> firstStart(
            @Unit(value = TEXT_PARSER_OWNER_PROJECT) MainUnit<Mail> textParserOwnerProject
//            @Unit(GUIDE_START) MainUnit<Mail> guideStart
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
                                inlineKeyBoard(
                                        simpleButton("start setting", TEXT_PARSER_PRIVATE_PROJECT)
//                                        simpleButton("see guide", GUIDE_START)
                                )
                        )
                )
                .next(textParserOwnerProject)
//                .next(guideStart)
                .build();
    }

    @Unit(value = TEXT_PARSER_OWNER_PROJECT)
    public AnswerText<Mail> textParserOwnerProject(
            @Unit(CHECK_PARSE_OWNER_PROJECT_YES) MainUnit<Mail> checkParseOwnerProjectYes,
            @Unit(CHECK_PARSE_OWNER_PROJECT_NO) MainUnit<Mail> checkParseOwnerProjectNo
    ) {
        return AnswerText.<Mail>builder()
                .answer(() -> replaceBoxAnswer(
                                """
                                        First of all, I suggest tracking changes in all repositories in which you are the creator.
                                                                                
                                        Find such repositories and set up notifications for them?
                                        """,
                                inlineKeyBoard(
                                        simpleLine(
                                                simpleButton("Yes", "YES"),
                                                simpleButton("No", "NO")
                                        )
                                )
                        )
                )
                .next(checkParseOwnerProjectYes)
                .next(checkParseOwnerProjectNo)
                .build();
    }

    @Unit(CHECK_PARSE_OWNER_PROJECT_NO)
    public AnswerText<Mail> checkParseOwnerProjectNo(
            @Unit(TEXT_AUTO_PARSE_OWNER_PROJECT) MainUnit<Mail> textAutoParsePublicProject
    ) {
        return AnswerText.<Mail>builder()
                .triggerCheck(clickButtonRaw("NO"))
                .answer(replaceBoxAnswer("\uD83D\uDC4C I won't scan public projects."))
                .<Integer>callBack(
                        sentBox -> scheduledExecutorService.schedule(() -> sending.deleteMessage(sentBox.getPersonId(), sentBox.getMessageId()), 10, TimeUnit.SECONDS)
                )
                .next(textAutoParsePublicProject)
                .build();
    }

    @Unit(CHECK_PARSE_OWNER_PROJECT_YES)
    public AnswerText<Mail> checkParseOwnerProjectYes(
            @Unit(TEXT_AUTO_PARSE_OWNER_PROJECT) MainUnit<Mail> textAutoParseOwnerProject
    ) {
        final String step1 = """
                🔘 Started searching for your repositories.
                ⌛ Wait...
                """;

        final String step2 = """
                🟢 {0} projects found.
                🔘 Scanning merge requests in found projects.
                ⌛ Wait...
                """;
        final String step3 = """
                🟢 {0} projects found.
                🟢 {1} merge requests found.
                🔘 Scanning pipelines in found merge requests.
                ⌛ Wait...
                """;

        final String step4 = """
                🟢 {0} projects found.
                🟢 {1} merge requests found.
                🟢 {2} pipelines found.
                🔘 Scanning threads in merge requests.
                ⌛ Wait...
                """;

        final String finalAnswer = """
                🟢 {0} projects found.
                🟢 {1} merge requests found.
                🟢 {2} pipelines found.
                🟢 {3} threads found.
                """;

        return AnswerText.<Mail>builder()
                .triggerCheck(clickButtonRaw("YES"))
                .answer(mail -> {
                    final String personId = mail.getPersonId();
                    final String messageId = Attachments.findFirstButtonClick(mail.getAttachments())
                            .map(ButtonClickAttachment::getMessageId)
                            .orElseThrow();
                    sending.replaceMessage(personId, messageId, boxAnswer(step1));

                    final int oldCountProjects = projectService.getAllIds().size();

                    projectParser.parseAllProjectOwner();
                    final Set<Long> projectIds = projectService.getAllIds();

                    projectService.notification(true, projectIds);
                    projectService.processing(true, projectIds);

                    final int projectCount = projectIds.size() - oldCountProjects;
                    sending.replaceMessage(personId, messageId, boxAnswer(format(step2, projectCount)));

                    final int oldCountMr = mergeRequestsService.getAllIds().size();
                    mergeRequestParser.parsingNewMergeRequest();
                    final int mrCount = mergeRequestsService.getAllIds().size() - oldCountMr;
                    sending.replaceMessage(personId, messageId, boxAnswer(format(step3, projectCount, mrCount)));

                    final int oldCountPipelines = pipelineService.getAllIds().size();

                    pipelineParser.scanNewPipeline();
                    final int pipelineCount = pipelineService.getAllIds().size() - oldCountPipelines;
                    sending.replaceMessage(personId, messageId, boxAnswer(format(step4, projectCount, mrCount, pipelineCount)));

                    final int oldCountThreads = discussionService.getAllIds().size();
                    discussionParser.scanNewDiscussion();
                    final int discussionCount = discussionService.getAllIds().size() - oldCountThreads;

                    return replaceBoxAnswer(format(finalAnswer, projectCount, mrCount, pipelineCount, discussionCount));
                })
                .<Integer>callBack(
                        sentBox -> scheduledExecutorService.schedule(() -> sending.deleteMessage(sentBox.getPersonId(), sentBox.getMessageId()), 10, TimeUnit.SECONDS)
                )
                .next(textAutoParseOwnerProject)
                .build();
    }

    @Unit(TEXT_AUTO_PARSE_OWNER_PROJECT)
    public AnswerText<Mail> textAutoParsePublicProject(
            @Unit(AUTO_PARSE_OWNER_PROJECT) MainUnit<Mail> autoParseOwnerProject
    ) {
        return AnswerText.<Mail>builder()
                .activeType(AFTER)
                .answer(
                        boxAnswer(
                                """
                                        By default, I do not notify about events in repositories that are not tracked. But you can turn on notifications for new repositories in which you are the creator.
                                                                                
                                        To do this?
                                        """,
                                inlineKeyBoard(
                                        simpleLine(
                                                simpleButton("Yes", "YES"),
                                                simpleButton("No", "NO")
                                        )
                                )
                        )
                )
                .next(autoParseOwnerProject)
                .build();
    }

    @Unit(AUTO_PARSE_OWNER_PROJECT)
    public AnswerText<Mail> autoParseOwnerProject(
            @Unit(TEXT_PARSER_PRIVATE_PROJECT) MainUnit<Mail> textParserPrivateProject
    ) {
        return AnswerText.<Mail>builder()
                .triggerCheck(isClickButton())
                .answer(mail -> {
                    final ButtonClickAttachment buttonClick = Attachments.findFirstButtonClick(mail.getAttachments()).orElseThrow();
                    if ("YES".equals(buttonClick.getRawCallBackData())) {
                        sending.replaceMessage(mail.getPersonId(), mail.getId(), boxAnswer("⌛I write down the available projects. This may take a long time."));
                        projectParser.parseAllProjectOwner();
                        settingService.ownerProjectScan(true);
                    } else {
                        settingService.ownerProjectScan(false);
                    }
                })
                .next(textParserPrivateProject)
                .build();
    }


    @Unit(value = TEXT_PARSER_PRIVATE_PROJECT)
    public AnswerText<Mail> textParserPrivateProject(
            @Unit(CHECK_PARSER_PRIVATE_PROJECT_YES) MainUnit<Mail> checkParserPrivateProjectYes,
            @Unit(CHECK_PARSER_PRIVATE_PROJECT_NO) MainUnit<Mail> checkParserPrivateProjectNo
    ) {
        return AnswerText.<Mail>builder()
                .activeType(AFTER)
                .answer(() -> replaceBoxAnswer(
                                """
                                        I can scan all your private projects and put them on tracking. This will notify you of new merge requests and other events. Or you can add only the projects you want later manually one by one.
                                                                        
                                        Add all available private projects?
                                        """,
                                inlineKeyBoard(
                                        simpleLine(
                                                simpleButton("Yes", "YES"),
                                                simpleButton("No", "NO")
                                        )
                                )
                        )
                )
                .next(checkParserPrivateProjectYes)
                .next(checkParserPrivateProjectNo)
                .build();
    }


    @Unit(CHECK_PARSER_PRIVATE_PROJECT_YES)
    public AnswerText<Mail> checkParserPrivateProjectYes(
            @Unit(TEXT_AUTO_PARSE_PRIVATE_PROJECT) MainUnit<Mail> textAutoParsePrivateProject
    ) {
        final String step1 = """
                🔘 Scanning of private projects has begun.
                ⌛ Wait...
                """;

        final String step2 = """
                🟢 {0} private projects found.
                🔘 Scanning merge requests in found projects.
                ⌛ Wait...
                """;
        final String step3 = """
                🟢 {0} private projects found.
                🟢 {1} merge requests found.
                🔘 Scanning pipelines in found merge requests.
                ⌛ Wait...
                """;

        final String step4 = """
                🟢 {0} private projects found.
                🟢 {1} merge requests found.
                🟢 {2} pipelines found.
                🔘 Scanning threads in merge requests.
                ⌛ Wait...
                """;

        final String finalAnswer = """
                🟢 {0} private projects found.
                🟢 {1} merge requests found.
                🟢 {2} pipelines found.
                🟢 {3} threads found.
                """;

        return AnswerText.<Mail>builder()
                .triggerCheck(clickButtonRaw("YES"))
                .answer(mail -> {
                    final String personId = mail.getPersonId();
                    final String messageId = Attachments.findFirstButtonClick(mail.getAttachments())
                            .map(ButtonClickAttachment::getMessageId)
                            .orElseThrow();
                    sending.replaceMessage(personId, messageId, boxAnswer(step1));

                    projectParser.parseAllPrivateProject();
                    final Set<Long> projectIds = projectService.getAllIds();

                    projectService.notification(true, projectIds);
                    projectService.processing(true, projectIds);

                    final int projectCount = projectIds.size();
                    sending.replaceMessage(personId, messageId, boxAnswer(format(step2, projectCount)));

                    mergeRequestParser.parsingNewMergeRequest();
                    final int mrCount = mergeRequestsService.getAllIds().size();
                    sending.replaceMessage(personId, messageId, boxAnswer(format(step3, projectCount, mrCount)));

                    pipelineParser.scanNewPipeline();
                    final int pipelineCount = pipelineService.getAllIds().size();
                    sending.replaceMessage(personId, messageId, boxAnswer(format(step4, projectCount, mrCount, pipelineCount)));

                    discussionParser.scanNewDiscussion();
                    final int discussionCount = discussionService.getAllIds().size();

                    return replaceBoxAnswer(format(finalAnswer, pipelineCount, mrCount, pipelineCount, discussionCount));
                })
                .<Integer>callBack(
                        sentBox -> scheduledExecutorService.schedule(() -> sending.deleteMessage(sentBox.getPersonId(), sentBox.getMessageId()), 10, TimeUnit.SECONDS)
                )
                .next(textAutoParsePrivateProject)
                .build();
    }

    @Unit(CHECK_PARSER_PRIVATE_PROJECT_NO)
    public AnswerText<Mail> checkParserPrivateProjectNo(
            @Unit(TEXT_AUTO_PARSE_PRIVATE_PROJECT) MainUnit<Mail> textAutoParsePrivateProject
    ) {
        return AnswerText.<Mail>builder()
                .triggerPhrase("NO")
                .answer(replaceBoxAnswer("\uD83D\uDC4C I won't scan private projects."))
                .<Integer>callBack(
                        sentBox -> scheduledExecutorService.schedule(() -> sending.deleteMessage(sentBox.getPersonId(), sentBox.getMessageId()), 10, TimeUnit.SECONDS)
                )
                .next(textAutoParsePrivateProject)
                .build();
    }

    @Unit(TEXT_AUTO_PARSE_PRIVATE_PROJECT)
    public AnswerText<Mail> textAutoParsePrivateProject(
            @Unit(AUTO_PARSE_PRIVATE_PROJECT) MainUnit<Mail> autoParsePrivateProject
    ) {
        return AnswerText.<Mail>builder()
                .activeType(AFTER)
                .answer(
                        boxAnswer(
                                """
                                        Do you want to enable automatic notification of new private projects available to you?
                                        -- -- -- -- --
                                        I will be forced to scan all available private projects for this. I will not scan other entities in projects and send any notifications for these projects.
                                        """,
                                inlineKeyBoard(
                                        simpleLine(
                                                simpleButton("Yes", "YES"),
                                                simpleButton("No", "NO")
                                        )
                                )
                        )
                )
                .next(autoParsePrivateProject)
                .build();
    }

    @Unit(AUTO_PARSE_PRIVATE_PROJECT)
    public AnswerText<Mail> autoParsePrivateProject(
            @Unit(END_SETTING) MainUnit<Mail> endSetting
    ) {
        return AnswerText.<Mail>builder()
                .triggerCheck(isClickButton())
                .answer(mail -> {
                    final ButtonClickAttachment buttonClick = Attachments.findFirstButtonClick(mail.getAttachments()).orElseThrow();
                    if ("YES".equals(buttonClick.getRawCallBackData())) {
                        sending.replaceMessage(mail.getPersonId(), mail.getId(), boxAnswer("⌛I write down the available private projects. This may take a long time."));
                        projectParser.parseAllPrivateProject();
                        settingService.privateProjectScan(true);
                    } else {
                        settingService.privateProjectScan(false);
                    }
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
                            settingService.turnOnAllNotify();
                            settingService.disableFirstStart();
                            return replaceBoxAnswer("""
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
