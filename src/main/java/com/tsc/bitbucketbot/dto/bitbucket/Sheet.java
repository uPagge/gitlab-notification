package com.tsc.bitbucketbot.dto.bitbucket;

import lombok.Data;

import java.util.List;

/**
 * TODO: Добавить описание класса.
 *
 * @author upagge [30.01.2020]
 */
@Data
public abstract class Sheet<T> {

    private Integer size;
    private Integer limit;
    private Boolean isLastPage;
    private List<T> values;
    private Integer nextPageStart;

    public boolean hasContent() {
        return values != null && !values.isEmpty();
    }

}
