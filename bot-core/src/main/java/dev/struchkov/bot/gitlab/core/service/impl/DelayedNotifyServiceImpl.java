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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DelayedNotifyServiceImpl {

    private final DelayedNotifyJpaRepository delayedNotifyJpaRepository;

    @Transactional
    public <T extends Notify> void save(T notify) {

        Gson gson = new Gson();
        DeferredMessage deferredMessage = new DeferredMessage();
        String notifyForSave = gson.toJson(notify);
        deferredMessage.setMessage(notifyForSave);
        deferredMessage.setTime(LocalDateTime.now().withNano(0));
        deferredMessage.setType(notify.getType());
        delayedNotifyJpaRepository.save(deferredMessage);
    }

    public List<Object> getAllNotify(){

        List<DeferredMessage> allDeferredMessageFromDb = delayedNotifyJpaRepository.findAll();

        return allDeferredMessageFromDb.stream()
                .map(DelayedNotifyServiceImpl::convertToNotifyClass).toList();
    }

    public static <T extends Notify> T convertToNotifyClass(DeferredMessage deferredMessage) {
        Gson gson = new Gson();
        Class<? extends Notify> newNotifyClass = switch (deferredMessage.getType()) {
            case ConflictMrNotify.TYPE -> ConflictMrNotify.class;
            case DiscussionNewNotify.TYPE -> DiscussionNewNotify.class;
            case NewCommentNotify.TYPE -> NewCommentNotify.class;
            case NewMrForAssignee.TYPE -> NewMrForAssignee.class;
            case NewMrForReview.TYPE -> NewMrForReview.class;
            case NewProjectNotify.TYPE -> NewProjectNotify.class;
            case PipelineNotify.TYPE -> PipelineNotify.class;
            case StatusMrNotify.TYPE -> StatusMrNotify.class;
            case TaskCloseNotify.TYPE -> TaskCloseNotify.class;
            case UpdateMrNotify.TYPE -> UpdateMrNotify.class;
            default -> throw new RuntimeException("the " + deferredMessage.getType() + " type class is not found");
        };

        Object notify = gson.fromJson(deferredMessage.getMessage(), newNotifyClass);
        return (T) notify;
    }


}
