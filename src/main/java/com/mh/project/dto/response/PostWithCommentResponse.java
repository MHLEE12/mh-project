package com.mh.project.dto.response;

import com.mh.project.dto.PostWithCommentDTO;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public record PostWithCommentResponse(
        Long id,
        String title,
        String content,
        String hashtag,
        LocalDateTime creDate,
        String email,
        String nickname,
        String userId,
        Set<CommentResponse> commentsResponse
) implements Serializable {

    public PostWithCommentResponse of(Long id, String title, String content, String hashtag, LocalDateTime creDate, String email, String nickname, String userId, Set<CommentResponse> commentResponses) {
        return new PostWithCommentResponse(
                id, title, content, hashtag, creDate, email, nickname, userId, commentResponses);
    }

    public static PostWithCommentResponse from(PostWithCommentDTO dto) {
        String nickname = dto.userAccountDTO().nickname();
        if (nickname == null || nickname.isBlank()) {
            nickname = dto.userAccountDTO().userId();
        }

        return new PostWithCommentResponse(
                dto.id(),
                dto.title(),
                dto.content(),
                dto.hashtag(),
                dto.creDate(),
                dto.userAccountDTO().email(),
                nickname,
                dto.userAccountDTO().userId(),
                dto.commentDTOs().stream()
                        .map(CommentResponse::from)
                        .collect(Collectors.toCollection(LinkedHashSet::new))
        );
    }
}
