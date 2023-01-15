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
    public static final String BUILD = "\uD83D\uDEE0";
    public static final String LINK = "\uD83D\uDD17";

    private Icons() {
        utilityClass();
    }

    public static String link(String title, String url) {
        return "[" + escapeMarkdown(title) + "](" + url + ")";
    }

}
