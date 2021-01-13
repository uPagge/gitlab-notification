package org.sadtech.bot.vsc.bitbucketbot.context.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

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
    KAKASHKA("\uD83D\uDCA9"),
    LUPA("\uD83D\uDD0D"),
    DANGEROUS("⚠️"),
    COMMENT("\uD83D\uDCAC"),
    ARROW("➜"),
    HR("\n -- -- -- -- --\n"),
    FAILURE("❌"),
    SUCCESS("✅"),
    BUILD("♻️"),
    SMART("\uD83E\uDDE0"),
    SADLY("\uD83D\uDE14"),
    TOP("\uD83D\uDD1D");

    @Getter
    private final String value;

    public static Smile statusPr(LocalDateTime updateDate) {
        int periodDay = Period.between(updateDate.toLocalDate(), LocalDate.now()).getDays();
        if (periodDay < 5) {
            return Smile.valueOf("DAY_" + periodDay);
        } else {
            return Smile.DAY_5;
        }
    }

    @Override
    public String toString() {
        return value;
    }

}
