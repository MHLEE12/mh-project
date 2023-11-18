package com.mh.project.dto.response;

import com.mh.project.dto.PostDTO;

import java.time.LocalDateTime;

public record PostResponse(
        Long id,
        String title,
        String content,
        String hashtag,
        LocalDateTime creDate,
        String email,
        String nickname
) {

    public static PostResponse of(Long id, String title, String content, String hashtag, LocalDateTime creDate, String email, String nickname) {
        return new PostResponse(id, title, content, hashtag, creDate, email, nickname);
    }

    public static PostResponse from(PostDTO dto) {
        String nickname = dto.userAccountDTO().nickname();
        if (nickname == null || nickname.isBlank()) {
            nickname = dto.userAccountDTO().userId();
        }

        return new PostResponse(
                dto.id(),
                dto.title(),
                dto.content(),
                dto.hashtag(),
                dto.creDate(),
                dto.userAccountDTO().email(),
                nickname
        );
    }
}
