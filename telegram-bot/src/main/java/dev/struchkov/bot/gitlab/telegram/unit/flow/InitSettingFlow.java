package dev.struchkov.bot.gitlab.telegram.unit.flow;

import dev.struchkov.bot.gitlab.context.domain.PersonInformation;
import dev.struchkov.bot.gitlab.context.domain.notify.level.DiscussionLevel;
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
import dev.struchkov.godfather.simple.domain.BoxAnswer;
import dev.struchkov.godfather.simple.domain.unit.AnswerText;
import dev.struchkov.godfather.simple.domain.unit.MainUnit;
import dev.struchkov.godfather.telegram.domain.attachment.ButtonClickAttachment;
import dev.struchkov.godfather.telegram.main.core.util.Attachments;
import dev.struchkov.godfather.telegram.simple.context.service.TelegramSending;
import dev.struchkov.godfather.telegram.starter.UnitConfiguration;
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
import static dev.struchkov.bot.gitlab.telegram.utils.UnitName.PRIVACY_SETTING_THREAD_LEVEL;
import static dev.struchkov.bot.gitlab.telegram.utils.UnitName.TEXT_AUTO_PARSE_OWNER_PROJECT;
import static dev.struchkov.bot.gitlab.telegram.utils.UnitName.TEXT_AUTO_PARSE_PRIVATE_PROJECT;
import static dev.struchkov.bot.gitlab.telegram.utils.UnitName.TEXT_PARSER_OWNER_PROJECT;
import static dev.struchkov.bot.gitlab.telegram.utils.UnitName.TEXT_PARSER_PRIVATE_PROJECT;
import static dev.struchkov.bot.gitlab.telegram.utils.UnitName.TEXT_PRIVACY_SETTING;
import static dev.struchkov.bot.gitlab.telegram.utils.UnitName.TEXT_PRIVACY_SETTING_THREAD_LEVEL;
import static dev.struchkov.godfather.main.domain.keyboard.button.SimpleButton.simpleButton;
import static dev.struchkov.godfather.main.domain.keyboard.simple.SimpleKeyBoardLine.simpleLine;
import static dev.struchkov.godfather.main.domain.unit.UnitActiveType.AFTER;
import static dev.struchkov.godfather.simple.domain.BoxAnswer.boxAnswer;
import static dev.struchkov.godfather.simple.domain.BoxAnswer.replaceBoxAnswer;
import static dev.struchkov.godfather.telegram.domain.keyboard.InlineKeyBoard.inlineKeyBoard;
import static dev.struchkov.godfather.telegram.main.context.BoxAnswerPayload.DISABLE_WEB_PAGE_PREVIEW;
import static dev.struchkov.godfather.telegram.main.core.util.InlineKeyBoards.verticalMenuButton;
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
public class InitSettingFlow implements UnitConfiguration {

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
                        BoxAnswer.builder().message(
                                        """
                                                Hello 👋
                                                
                                                This bot will help you keep your finger on the pulse of all your GitLab projects.
                                                                                        
                                                ❓*How it works*
                                                Every few minutes I poll the Gitlab API using your token. I get information about repositories, merge requests in them, trades, pipelines, and other stuff. Some of the information I save to my database. This allows me to track changes and create notifications about them. All of the data is stored with you and is not shared anywhere. I also try to remove data that is not needed for work, such as closed merge requests.
                                                                                        
                                                🥷*Privacy*
                                                Some data may be very sensitive to send to the Telegram servers. For example, discussions in the Merge Requests threads. During setup, you will be able to select the privacy level of different types of notifications. The higher the level, the less sensitive data will be in the notification.
                                                -- -- -- -- --
                                                🏠 [Home Page](https://git.struchkov.dev/Telegram-Bots/gitlab-notification) • 🐛 [Issues](https://github.com/uPagge/gitlab-notification/issues) • 🛣 [Road Map](https://git.struchkov.dev/Telegram-Bots/gitlab-notification/issues)
                                                -- -- -- -- --
                                                👇Press start to start initial setup 👇
                                                """
                                )
                                .payload(DISABLE_WEB_PAGE_PREVIEW, true)
                                .keyBoard(
                                        inlineKeyBoard(
                                                simpleButton("\uD83D\uDE80 Start setting", TEXT_PARSER_PRIVATE_PROJECT)
//                                        simpleButton("see guide", GUIDE_START)
                                        )
                                )
                                .build()
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
                                        Would you like me to notify you of any events in the repositories where you are the creator?
                                        
                                        You can add projects later, but it will be less convenient.
                                        -- -- -- -- --
                                        We'll set privacy levels later, don't worry 😌
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
                                        I only send notifications for repositories that have been put on track. However, I can notify you when new repositories are available. This will allow you to track new repositories you are interested in quickly.
                                        
                                        Start keeping track of new repositories where you are the creator?
                                        -- -- -- -- --
                                        If you answer yes, then I will be forced to add all available repositories that you own to the database, even if you answered no in the last paragraph. This is the only way I will be able to notify you of new repositories.
                                        
                                        Don't worry, I will not scan these repositories and notify you about them in the future.
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
                        sending.replaceMessage(mail.getPersonId(), mail.getId(), boxAnswer("I write down the available projects.\nThis may take a long time ⌛"));
                        projectParser.parseAllProjectOwner();
                        settingService.ownerProjectScan(true);
                    } else {
                        settingService.ownerProjectScan(false);
                    }
                })
                .callBack(sentBox -> scheduledExecutorService.schedule(() -> sending.deleteMessage(sentBox.getPersonId(), sentBox.getMessageId()), 6, TimeUnit.SECONDS))
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
                                        Do you want me to find all the private repositories available to you and put them on track?
                                        -- -- -- -- --
                                        ⚠️ If there are a lot of repositories, this might not be a good idea. Only track repositories in which you actively participate. This will reduce the number of requests to the GitLab API.
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
            @Unit(TEXT_PRIVACY_SETTING) MainUnit<Mail> textPrivacySetting
    ) {
        return AnswerText.<Mail>builder()
                .triggerCheck(isClickButton())
                .answer(mail -> {
                    final ButtonClickAttachment buttonClick = Attachments.findFirstButtonClick(mail.getAttachments()).orElseThrow();
                    if ("YES".equals(buttonClick.getRawCallBackData())) {
                        sending.replaceMessage(mail.getPersonId(), mail.getId(), boxAnswer("I write down the available private projects.\nThis may take a long time ⌛"));
                        projectParser.parseAllPrivateProject();
                        settingService.privateProjectScan(true);
                    } else {
                        settingService.privateProjectScan(false);
                    }
                })
                .callBack(sentBox -> scheduledExecutorService.schedule(() -> sending.deleteMessage(sentBox.getPersonId(), sentBox.getMessageId()), 6, TimeUnit.SECONDS))
                .next(textPrivacySetting)
                .build();
    }

    @Unit(TEXT_PRIVACY_SETTING)
    public AnswerText<Mail> textSetLevelThread(
            @Unit(TEXT_PRIVACY_SETTING_THREAD_LEVEL) MainUnit<Mail> textPrivacySettingThreadLevel
    ) {
        return AnswerText.<Mail>builder()
                .activeType(AFTER)
                .answer(
                        replaceBoxAnswer("""
                                        Each company and/or team has its own level of confidentiality. You probably don't want to trust any information to Telegram, because all messages will go through its servers. Also, your telegame account can be hacked.
                                                                
                                        So now we will set up the privacy of the notifications you receive.
                                        """,
                                inlineKeyBoard(
                                        simpleLine(
                                                simpleButton("\uD83E\uDD77 start setting up \uD83E\uDD77", "start setting up")
                                        )
                                )
                        )
                )
                .next(textPrivacySettingThreadLevel)
                .build();
    }

    @Unit(TEXT_PRIVACY_SETTING_THREAD_LEVEL)
    public AnswerText<Mail> textPrivacySettingThreadLevel(
            @Unit(PRIVACY_SETTING_THREAD_LEVEL) MainUnit<Mail> privacySettingThreadLevel
    ) {
        return AnswerText.<Mail>builder()
                .answer(
                        replaceBoxAnswer("""
                                        A lot of confidential information can be contained in notifications of posts in a thread.
                                                                                
                                        Choose a privacy level:
                                        
                                        WITHOUT NOTIFY - turn off notifications for threads
                                        
                                        NOTIFY WITHOUT CONTEXT - notifications about the presence of a new comment with a link, without the comment text itself
                                        
                                        NOTIFY WITH CONTEXT - notification of new comments along with comment text
                                        """,
                                verticalMenuButton(
                                        simpleButton("WITHOUT NOTIFY", "WITHOUT_NOTIFY"),
                                        simpleButton("NOTIFY WITHOUT CONTEXT", "NOTIFY_WITHOUT_CONTEXT"),
                                        simpleButton("NOTIFY WITH CONTEXT", "NOTIFY_WITH_CONTEXT")
                                )
                        )
                )
                .next(privacySettingThreadLevel)
                .build();
    }

    @Unit(PRIVACY_SETTING_THREAD_LEVEL)
    public AnswerText<Mail> privacySettingThreadLevel(
            @Unit(END_SETTING) MainUnit<Mail> endSetting
    ) {
        return AnswerText.<Mail>builder()
                .answer(mail -> {
                    final ButtonClickAttachment buttonClick = Attachments.findFirstButtonClick(mail.getAttachments()).orElseThrow();
                    final DiscussionLevel level = DiscussionLevel.valueOf(buttonClick.getRawCallBackData().toUpperCase());
                    settingService.setDiscussionLevel(level);
                    replaceBoxAnswer("\uD83D\uDC4D you have successfully set the privacy level for threads");
                })
                .callBack(
                        sentBox -> scheduledExecutorService.schedule(() -> sending.deleteMessage(sentBox.getPersonId(), sentBox.getMessageId()), 6, TimeUnit.SECONDS)
                )
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
                                    inlineKeyBoard(simpleButton("Open General Menu", "/start"))
                            );
                        }
                )
                .build();
    }

}
