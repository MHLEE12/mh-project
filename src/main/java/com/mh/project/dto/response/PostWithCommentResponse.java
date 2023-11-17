package com.mh.project.dto.response;

import com.mh.project.dto.PostWithCommentDTO;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

public record PostWithCommentResponse(
        Long id,
        String title,
        String content,
        String hashtag,
        LocalDateTime creDate,
        String email,
        String nickname,
        Set<PostCommentResponse> postCommentResponses
) implements Serializable {

    public PostWithCommentResponse of(Long id, String title, String content, String hashtag, LocalDateTime creDate, String email, String nickname, Set<PostCommentResponse> postCommentResponses) {
        return new PostWithCommentResponse(
                id, title, content, hashtag, creDate, email, nickname, postCommentResponses);
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
                dto.
        );
    }
}
