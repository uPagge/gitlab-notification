package com.tsc.bitbucketbot.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * TODO: Добавить описание класса.
 *
 * @author upagge [03.02.2020]
 */
@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Pagination {

    private Integer page;
    private Integer size;

    public static Pagination of(Integer page, Integer size) {
        return new Pagination(page, size);
    }


}
