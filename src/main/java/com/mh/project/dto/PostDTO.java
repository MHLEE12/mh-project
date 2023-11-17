package com.mh.project.dto;

import com.mh.project.domain.Post;

import java.time.LocalDateTime;

/**
 * DTO for {@link com.mh.project.domain.Post}
 */
public record PostDTO(
        Long id,
        UserAccountDTO userAccountDTO,
        String title,
        String content,
        String hashtag,
        LocalDateTime creDate,
        String creUser,
        LocalDateTime modDate,
        String modUser
) {

    public static PostDTO of(Long id, UserAccountDTO userAccountDTO, String title, String content, String hashtag, LocalDateTime creDate, String creUser, LocalDateTime modDate, String modUser) {
        return new PostDTO(id, userAccountDTO, title, content, hashtag, creDate, creUser, modDate, modUser);
    }

    public static PostDTO from(Post entity) {
        return new PostDTO(
                entity.getId(),
                UserAccountDTO.from(entity.getUserAccount()),
                entity.getTitle(),
                entity.getContent(),
                entity.getHashtag(),
                entity.getCreDate(),
                entity.getCreUser(),
                entity.getModDate(),
                entity.getModUser()
        );
    }

    public Post toEntity() {
        return Post.of(
                userAccountDTO.toEntity(),
                title,
                content,
                hashtag
        );
    }
}