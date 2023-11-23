package com.mh.project.dto.response;

import com.mh.project.dto.CommentDTO;

import java.io.Serializable;
import java.time.LocalDateTime;

public record CommentResponse(
        Long id,
        String content,
        LocalDateTime creDate,
        String email,
        String nickname,
        String userId
) {

    public static CommentResponse of(Long id, String content, LocalDateTime creDate, String email, String nickname, String userId) {
        return new CommentResponse(id, content, creDate, email, nickname, userId);
    }

    public static CommentResponse from(CommentDTO dto) {
        String nickname = dto.userAccountDTO().nickname();
        if (nickname == null || nickname.isBlank()) {
            nickname = dto.userAccountDTO().userId();
        }

        return new CommentResponse(
                dto.id(),
                dto.content(),
                dto.creDate(),
                dto.userAccountDTO().email(),
                nickname,
                dto.userAccountDTO().userId()
        );
    }
}
