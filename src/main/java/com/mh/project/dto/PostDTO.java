package com.mh.project.dto;

import com.mh.project.domain.Post;
import com.mh.project.domain.UserAccount;

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

    public static PostDTO of(UserAccountDTO userAccountDTO, String title, String content, String hashtag) {
        return new PostDTO(null, userAccountDTO, title, content, hashtag, null, null, null, null);
    }

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

    public Post toEntity(UserAccount userAccount) {
        return Post.of(
                userAccount,
                title,
                content,
                hashtag
        );
    }
}