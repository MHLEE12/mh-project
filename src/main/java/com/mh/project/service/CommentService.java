package com.mh.project.service;

import com.mh.project.dto.CommentDTO;
import com.mh.project.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
public class CommentService {

    private final CommentRepository commentRepository;

    @Transactional(readOnly = true)
    public List<CommentDTO> searchComments(Long postId) {
        return List.of();
    }

    public void saveComment(CommentDTO comment) {
    }

    public void updateComment(CommentDTO dto) {
    }

    public void deleteComment(Long commentId) {
    }
}
