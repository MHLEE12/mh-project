package com.mh.project.dto.response;

import com.mh.project.dto.PostCommentDTO;

import java.io.Serializable;
import java.time.LocalDateTime;

public record PostCommentResponse(
        Long id,
        String content,
        LocalDateTime creDate,
        String email,
        String nickname
) implements Serializable {

    public static PostCommentResponse of(Long id, String content, LocalDateTime creDate, String email, String nickname) {
        return new PostCommentResponse(id, content, creDate, email, nickname);
    }

    public static PostCommentResponse from(PostCommentDTO dto) {

    }
}
