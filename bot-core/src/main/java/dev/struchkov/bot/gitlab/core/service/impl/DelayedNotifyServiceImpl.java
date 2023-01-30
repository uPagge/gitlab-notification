package dev.struchkov.bot.gitlab.core.service.impl;

import com.google.gson.Gson;
import dev.struchkov.bot.gitlab.context.domain.entity.DeferredMessage;
import dev.struchkov.bot.gitlab.context.domain.notify.Notify;
import dev.struchkov.bot.gitlab.context.domain.notify.comment.NewCommentNotify;
import dev.struchkov.bot.gitlab.context.domain.notify.mergerequest.*;
import dev.struchkov.bot.gitlab.context.domain.notify.pipeline.PipelineNotify;
import dev.struchkov.bot.gitlab.context.domain.notify.project.NewProjectNotify;
import dev.struchkov.bot.gitlab.context.domain.notify.task.DiscussionNewNotify;
import dev.struchkov.bot.gitlab.context.domain.notify.task.TaskCloseNotify;
import dev.struchkov.bot.gitlab.data.jpa.DelayedNotifyJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DelayedNotifyServiceImpl {

    private final DelayedNotifyJpaRepository delayedNotifyJpaRepository;

    public <T extends Notify> void save(T notify) {

        Gson gson = new Gson();
        DeferredMessage deferredMessage = new DeferredMessage();
        String notifyForSave = gson.toJson(notify);
        deferredMessage.setMessage(notifyForSave);
        deferredMessage.setTime(LocalDateTime.now().plusNanos(0));
        delayedNotifyJpaRepository.save(deferredMessage);
    }

    public <T extends Notify> void getNotifyFromDb(T notify){
        Class<? extends Notify> newNotify;

        switch (notify.getType()) {
            case ConflictMrNotify.TYPE:
                newNotify = ConflictMrNotify.class;
                break;
            case DiscussionNewNotify.TYPE:
                newNotify = DiscussionNewNotify.class;
                break;
            case NewCommentNotify.TYPE:
                newNotify = NewCommentNotify.class;
                break;
            case NewMrForAssignee.TYPE:
                newNotify = NewMrForAssignee.class;
                break;
            case NewMrForReview.TYPE:
                newNotify = NewMrForReview.class;
                break;
            case NewProjectNotify.TYPE:
                newNotify = NewProjectNotify.class;
                break;
            case PipelineNotify.TYPE:
                newNotify = PipelineNotify.class;
                break;
            case StatusMrNotify.TYPE:
                newNotify = StatusMrNotify.class;
                break;
            case TaskCloseNotify.TYPE:
                newNotify = TaskCloseNotify.class;
                break;
            case UpdateMrNotify.TYPE:
                newNotify = UpdateMrNotify.class;
                break;
        }
    }

}
