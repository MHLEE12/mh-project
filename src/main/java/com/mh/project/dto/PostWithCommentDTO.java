package com.mh.project.dto;

import com.mh.project.domain.Post;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public record PostWithCommentDTO(
        Long id,
        UserAccountDTO userAccountDTO,
        Set<CommentDTO> commentDTOs,
        String title,
        String content,
        String hashtag,
        LocalDateTime creDate,
        String creUser,
        LocalDateTime modDate,
        String modUser
) {
    public static PostWithCommentDTO of(Long id, UserAccountDTO userAccountDTO, Set<CommentDTO> commentDTOs, String title, String content, String hashtag, LocalDateTime creDate, String creUser, LocalDateTime modDate, String modUser) {
        return new PostWithCommentDTO(id, userAccountDTO, commentDTOs, title, content, hashtag, creDate, creUser, modDate, modUser);
    }

    public static PostWithCommentDTO from(Post entity) {
        return new PostWithCommentDTO(
                entity.getId(),
                UserAccountDTO.from(entity.getUserAccount()),
                entity.getComment().stream()
                        .map(CommentDTO::from)
                        .collect(Collectors.toCollection(LinkedHashSet::new)),
                entity.getTitle(),
                entity.getContent(),
                entity.getHashtag(),
                entity.getCreDate(),
                entity.getCreUser(),
                entity.getModDate(),
                entity.getModUser()
        );
    }
}
