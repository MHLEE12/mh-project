package com.mh.project.dto;

import com.mh.project.domain.PostComment;

import java.time.LocalDateTime;

public record PostCommentDTO(
        Long id,
        Long postId,
        UserAccountDTO userAccountDTO,
        String content,
        LocalDateTime creDate,
        String creUser,
        LocalDateTime modDate,
        String modUser
) {

    public PostCommentDTO of(Long id, Long postId, UserAccountDTO userAccountDTO, String content, LocalDateTime creDate, String creUser, LocalDateTime modDate, String modUser) {
        return new PostCommentDTO(id, postId, userAccountDTO, content, creDate, creUser, modDate, modUser);
    }

    public static PostCommentDTO from(PostComment entity) {
        return new PostCommentDTO(
                entity.getId(),
                entity.getPost().getId(),
                UserAccountDTO.from(entity.getUserAccount()),
                entity.getContent(),
                entity.getCreDate(),
                entity.getCreUser(),
                entity.getModDate(),
                entity.getModUser()
        );
    }
}
