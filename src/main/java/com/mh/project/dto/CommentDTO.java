package com.mh.project.dto;

import com.mh.project.domain.Comment;
import com.mh.project.domain.Post;

import java.time.LocalDateTime;

/**
 * DTO for {@link com.mh.project.domain.Comment}
 */
public record CommentDTO(
        Long id,
        Long postId,
        UserAccountDTO userAccountDTO,
        String content,
        LocalDateTime creDate,
        String creUser,
        LocalDateTime modDate,
        String modUser) {

    public static CommentDTO of(Long id, Long articleId, UserAccountDTO userAccountDto, String content, LocalDateTime creDate, String creUser, LocalDateTime modDate, String modUser) {
        return new CommentDTO(id, articleId, userAccountDto, content, creDate, creUser, modDate, modUser);
    }

    public static CommentDTO from(Comment entity) {
        return new CommentDTO(
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

    public Comment toEntity(Post entity) {
        return Comment.of(
                entity,
                userAccountDTO.toEntity(),
                content
        );
    }
}