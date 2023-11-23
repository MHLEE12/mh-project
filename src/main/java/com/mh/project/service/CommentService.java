package com.mh.project.service;

import com.mh.project.domain.Comment;
import com.mh.project.domain.Post;
import com.mh.project.domain.UserAccount;
import com.mh.project.dto.CommentDTO;
import com.mh.project.repository.CommentRepository;
import com.mh.project.repository.PostRepository;
import com.mh.project.repository.UserAccountRepository;
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
    private final UserAccountRepository userAccountRepository;

    @Transactional(readOnly = true)
    public List<CommentDTO> searchComments(Long postId) {
        return commentRepository.findByPostId(postId)
                .stream()
                .map(CommentDTO::from)
                .toList();
    }

    public void saveComment(CommentDTO dto) {
        try {
            Post post = postRepository.getReferenceById(dto.postId());
            UserAccount userAccount = userAccountRepository.getReferenceById(dto.userAccountDTO().userId());
            commentRepository.save(dto.toEntity(post, userAccount));
        } catch (EntityNotFoundException e) {
            log.warn("댓글 저장 실패. 댓글 작성에 필요한 정보를 찾을 수 없습니다. - {}", e.getLocalizedMessage());
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

    public void deleteComment(Long commentId, String userId) {
        commentRepository.deleteByIdAndUserAccount_UserId(commentId, userId);
    }
}
