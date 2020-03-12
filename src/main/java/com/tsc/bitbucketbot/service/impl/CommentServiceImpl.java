package com.tsc.bitbucketbot.service.impl;

import com.tsc.bitbucketbot.domain.entity.TechInfo;
import com.tsc.bitbucketbot.repository.jpa.TechInfoRepository;
import com.tsc.bitbucketbot.service.CommentService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final TechInfoRepository techInfoRepository;

    @Override
    public Long getLastCommentId() {
        final Optional<TechInfo> optLastCommentId = techInfoRepository.findById(1L);
        return optLastCommentId.isPresent() ? optLastCommentId.get().getLastCommentId() : 0L;
    }

    @Override
    public void saveLastCommentId(@NonNull Long commentId) {
        techInfoRepository.saveAndFlush(new TechInfo(1L, commentId));
    }

}
