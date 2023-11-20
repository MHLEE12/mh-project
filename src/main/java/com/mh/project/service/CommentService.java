package com.mh.project.service;

import com.mh.project.domain.Comment;
import com.mh.project.dto.CommentDTO;
import com.mh.project.repository.CommentRepository;
import com.mh.project.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class CommentService {
    private final PostRepository postRepository;

    private final CommentRepository commentRepository;

    @Transactional(readOnly = true)
    public List<CommentDTO> searchComments(Long postId) {
        return commentRepository.findByPostId(postId)
                .stream()
                .map(CommentDTO::from)
                .toList();
    }

    public void saveComment(CommentDTO dto) {
        try {
            commentRepository.save(dto.toEntity(postRepository.getReferenceById(dto.postId())));
        } catch (EntityNotFoundException e) {
            log.warn("댓글 저장 실패. 댓글을 저장할 게시글을 찾을 수 없습니다. -DTO: {}", dto);
        }
    }

    public void updateComment(CommentDTO dto) {
        try {
            Comment comment = commentRepository.getReferenceById(dto.id());
            if (dto.content() != null) {
               comment.setContent(dto.content());
            }
        } catch (EntityNotFoundException e) {
            log.warn("댓글 수정 실패. 댓글을 찾을 수 없습니다. -DTO: {}", dto);
        }
    }

    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }
}
