package com.tsc.bitbucketbot.utils;

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
    MEGA_FUN("\uD83D\uDE02"),
    DANGEROUS("⚠️"),
    BELL("\uD83D\uDECE"),
    HR("\n -- -- -- -- --\n");

    @Getter
    private String value;

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

    public static class Constants {
        public static final String EMPTY = "";
    }

}
