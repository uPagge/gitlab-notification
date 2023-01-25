package dev.struchkov.bot.gitlab.context.utils;

import static dev.struchkov.haiti.utils.Exceptions.utilityClass;
import static dev.struchkov.haiti.utils.Strings.escapeMarkdown;

public class Icons {

    public static final String HR = "\n-- -- -- -- --\n";

    public static final String FUN = "\uD83C\uDF89";
    public static final String VIEW = "\uD83D\uDC40";
    public static final String TREE = "\uD83C\uDF33";
    public static final String AUTHOR = "\uD83D\uDC68\u200D\uD83D\uDCBB️";
    public static final String UPDATE = "\uD83D\uDD04";
    public static final String COMMENT = "\uD83D\uDCAC";
    public static final String TASK = "\uD83D\uDCBC";
    public static final String ARROW = " ➜ ";
    public static final String DANGEROUS = "⚠️";
    public static final String PEN = "✏️";
    public static final String ASSIGNEE = "\uD83C\uDFA9";
    public static final String BUILD = "⚙️";
    public static final String LINK = "\uD83D\uDD17";
    public static final String REVIEWER = "\uD83D\uDD0E";
    public static final String PROJECT = "\uD83C\uDFD7";
    public static final String DISABLE_NOTIFY = "\uD83D\uDD15";
    public static final String YES = "✅";
    public static final String NO = "❌";
    public static final String NOTIFY = "\uD83D\uDD14";
    public static final String NO_PROCESSING = "\uD83D\uDDD1";
    public static final String GOOD = "\uD83D\uDC4D";

    private Icons() {
        utilityClass();
    }

    public static String link(String title, String url) {
        return "[" + escapeMarkdown(title) + "](" + url + ")";
    }

}
