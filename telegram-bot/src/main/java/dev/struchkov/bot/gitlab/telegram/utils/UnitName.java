package dev.struchkov.bot.gitlab.telegram.utils;

import static dev.struchkov.haiti.utils.Exceptions.utilityClass;

public final class UnitName {

    public static final String GENERAL_MENU = "generalMenu";
    public static final String TEXT_ADD_NEW_PROJECT = "textAddNewProject";
    public static final String ADD_NEW_PROJECT = "addNewProject";
    public static final String SETTINGS = "settings";
    public static final String GET_TASKS = "getTasks";
    public static final String GET_ASSIGNEE_MERGE_REQUEST = "getAssigneeMergeRequest";
    public static final String CHECK_FIRST_START = "checkFirstStart";
    public static final String CHECK_MENU_OR_ANSWER = "checkMenuOrAnswer";
    public static final String ANSWER_NOTE = "answerNote";
    public static final String TEXT_PARSER_PRIVATE_PROJECT = "textParserPrivateProject";
    public static final String CHECK_PARSER_PRIVATE_PROJECT = "checkParserPrivateProject";
    public static final String PARSER_PRIVATE_PROJECT = "parserPrivateProject";
    public static final String TEXT_PARSE_OWNER_PROJECT = "textParseOwnerProject";
    public static final String CHECK_PARSE_OWNER_PROJECT = "checkParseOwnerProject";
    public static final String PARSE_OWNER_PROJECT = "parseOwnerProject";
    public static final String END_SETTING = "endSetting";
    public static final String AUTHORIZATION = "AUTHORIZATION";
    public static final String ACCESS_ERROR = "ACCESS_ERROR";

    private UnitName() {
        utilityClass();
    }

}
