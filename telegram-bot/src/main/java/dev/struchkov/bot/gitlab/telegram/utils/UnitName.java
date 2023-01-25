package dev.struchkov.bot.gitlab.telegram.utils;

import static dev.struchkov.haiti.utils.Exceptions.utilityClass;

public final class UnitName {

    public static final String GENERAL_MENU = "generalMenu";
    public static final String TEXT_ADD_NEW_PROJECT = "textAddNewProject";
    public static final String ADD_NEW_PROJECT = "addNewProject";
    public static final String SETTINGS = "settings";
    public static final String GET_TASKS = "getTasks";
    public static final String GET_ASSIGNEE_MERGE_REQUEST = "getAssigneeMergeRequest";
    public static final String FIRST_START = "checkFirstStart";
    public static final String ANSWER_NOTE = "answerNote";
    public static final String TEXT_PARSER_PRIVATE_PROJECT = "textParserPrivateProject";
    public static final String CHECK_PARSER_PRIVATE_PROJECT_YES = "checkParserPrivateProject";
    public static final String TEXT_PARSE_OWNER_PROJECT = "textParseOwnerProject";
    public static final String CHECK_PARSE_OWNER_PROJECT_YES = "checkParseOwnerProject";
    public static final String END_SETTING = "endSetting";
    public static final String ACCESS_ERROR = "ACCESS_ERROR";
    public static final String CHECK_PARSER_PRIVATE_PROJECT_NO = "CHECK_PARSER_PRIVATE_PROJECT_NO";
    public static final String CHECK_PARSE_OWNER_PROJECT_NO = "CHECK_PARSE_OWNER_PROJECT_NO";
    public static final String TEXT_AUTO_PARSE_PRIVATE_PROJECT = "TEXT_AUTO_PARSE_PRIVATE_PROJECT";
    public static final String AUTO_PARSE_PRIVATE_PROJECT = "AUTO_PARSE_PRIVATE_PROJECT";
    public static final String AUTO_PARSE_OWNER_PROJECT = "AUTO_PARSE_PUBLIC_PROJECT";
    public static final String TEXT_AUTO_PARSE_OWNER_PROJECT = "TEXT_AUTO_PARSE_OWNER_PROJECT";
    public static final String GUIDE_START = "GUIDE_START";
    public static final String TEXT_PARSER_OWNER_PROJECT = "TEXT_PARSER_OWNER_PROJECT";

    // команды
    public static final String DELETE_MESSAGE = "DELETE_MESSAGE";
    public static final String DISABLE_NOTIFY_MR = "DISABLE_NOTIFY_MR";
    public static final String ENABLE_NOTIFY_PROJECT = "ENABLE_NOTIFY_PROJECT";

    private UnitName() {
        utilityClass();
    }

}
