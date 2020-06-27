package org.sadtech.bot.bitbucketbot.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.sadtech.bot.bitbucketbot.scheduler.SchedulerComments;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Данные для конфигурации {@link SchedulerComments}
 *
 * @author upagge
 */
@Getter
@Setter
@Component
@ConfigurationProperties("bitbucketbot.scheduler.comment.settings")
public class CommentSchedulerProperty {

    /**
     * Количество пустых комментариев подряд, после которого поиск останавливается
     */
    private Integer noCommentCount;

    /**
     * Количество комментариев в пачке сканирования
     */
    private Integer commentCount;

}
