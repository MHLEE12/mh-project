package com.mh.project.dto.request;

import com.mh.project.dto.CommentDTO;
import com.mh.project.dto.UserAccountDTO;

import java.io.Serializable;

/**
 * DTO for {@link com.mh.project.domain.Comment}
 */
public record CommentRequest(Long postId, String content) {

    public static CommentRequest of(Long postId, String content) {
        return new CommentRequest(postId, content);
    }

    public CommentDTO toDTO(UserAccountDTO userAccountDTO) {
        return CommentDTO.of(
                postId,
                userAccountDTO,
                content
        );
    }
}
