package com.mh.project.dto;

import com.mh.project.domain.Comment;
import com.mh.project.domain.Post;
import com.mh.project.domain.UserAccount;

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

    public static CommentDTO of(Long postId, UserAccountDTO userAccountDto, String content) {
        return new CommentDTO(null, postId, userAccountDto, content, null, null, null, null);
    }

    public static CommentDTO of(Long id, Long postId, UserAccountDTO userAccountDto, String content, LocalDateTime creDate, String creUser, LocalDateTime modDate, String modUser) {
        return new CommentDTO(id, postId, userAccountDto, content, creDate, creUser, modDate, modUser);
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

    public Comment toEntity(Post post, UserAccount userAccount) {
        return Comment.of(
                post,
                userAccount,
                content
        );
    }
}