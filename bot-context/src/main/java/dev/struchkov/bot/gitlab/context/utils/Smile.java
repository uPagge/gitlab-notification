package dev.struchkov.bot.gitlab.context.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum Smile {

    BR("\n"),
    TWO_BR("\n\n"),
    AUTHOR("\uD83D\uDC68\u200D\uD83D\uDCBB️"),
    PEN("✏️"),
    FUN("\uD83C\uDF89"),
    UPDATE("\uD83D\uDD04"),
    SUN("\uD83D\uDD06"),
    MIG("\uD83D\uDE09"),
    BUY("\uD83D\uDC4B"),
    FLOWER("\uD83C\uDF40"),
    DAY_0("\uD83C\uDF15"),
    DAY_1("\uD83C\uDF16"),
    DAY_2("\uD83C\uDF17"),
    DAY_3("\uD83C\uDF18"),
    DAY_4("\uD83C\uDF11"),
    DAY_5("\uD83C\uDF1A"),
    TASK("\uD83D\uDCBC"),
    TOP_ONE("\uD83C\uDF1F\uD83C\uDF1F\uD83C\uDF1F"),
    TOP_TWO("\uD83D\uDE0E"),
    TOP_THREE("\uD83E\uDD49"),
    DANGEROUS("⚠️"),
    COMMENT("\uD83D\uDCAC"),
    ARROW("➜"),
    SHORT_HR("\n-- -- --\n"),
    HR("\n-- -- -- -- --\n"),
    HR2("\n-- -- -- -- -- -- -- -- -- --\n"),
    FAILURE("❌"),
    SUCCESS("✅"),
    BUILD("♻️"),
    SMART("\uD83E\uDDE0"),
    SADLY("\uD83D\uDE14"),
    TREE("\uD83C\uDF33"),
    TOP("\uD83D\uDD1D");

    @Getter
    private final String value;

    @Override
    public String toString() {
        return value;
    }

}
