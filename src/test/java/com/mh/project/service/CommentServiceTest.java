package com.mh.project.service;

import com.mh.project.domain.Comment;
import com.mh.project.domain.Post;
import com.mh.project.domain.UserAccount;
import com.mh.project.dto.CommentDTO;
import com.mh.project.dto.UserAccountDTO;
import com.mh.project.repository.CommentRepository;
import com.mh.project.repository.PostRepository;
import com.mh.project.repository.UserAccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@DisplayName("비즈니스 로직 - 댓글")
@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @InjectMocks private CommentService sut;

    @Mock private PostRepository postRepository;
    @Mock private CommentRepository commentRepository;
    @Mock private UserAccountRepository userAccountRepository;

    @DisplayName("게시글id로 해당 게시글의 댓글 리스트가 반환된다.")
    @Test
    void givenPostId_searchComment() {
        // Given
        Long postId = 1L;
        Comment newComment = createComment("content");
        given(commentRepository.findByPostId(postId)).willReturn(List.of(newComment));
        int haveComments = commentRepository.findByPostId(postId).size();

        // When
        List<CommentDTO> actual = sut.searchComments(postId);

        // Then
        assertThat(actual)
                .hasSize(haveComments)
                .first().hasFieldOrPropertyWithValue("content", newComment.getContent());
//        then(commentRepository).should().findByPostId(postId);
    }

    @DisplayName("댓글을 입력하면, 댓글이 저장된다.")
    @Test
    void writeComment_saveComment() {
        // Given
        CommentDTO dto = createCommentDTO("댓글");
        given(postRepository.getReferenceById(dto.postId())).willReturn(createPost());
        given(userAccountRepository.getReferenceById(dto.userAccountDTO().userId())).willReturn(createUserAccount());
        given(commentRepository.save(any(Comment.class))).willReturn(null);

        // When
        sut.saveComment(dto);

        // Then
        then(postRepository).should().getReferenceById(dto.postId());
        then(userAccountRepository).should().getReferenceById(dto.userAccountDTO().userId());
        then(commentRepository).should().save(any(Comment.class));
    }

    @DisplayName("댓글 저장 시 해당하는 게시글이 없으면, 경고 로그를 나타내고 아무런 작동을 하지 않는다.")
    @Test
    void nonePost_saveComment_warningLogAndDoNothing() {
        // Given
        CommentDTO dto = createCommentDTO("댓글");
        given(postRepository.getReferenceById(dto.postId())).willThrow(EntityNotFoundException.class);

        // When
        sut.saveComment(dto);

        // Then
        then(postRepository).should().getReferenceById(dto.postId());
        then(userAccountRepository).shouldHaveNoInteractions();
        then(commentRepository).shouldHaveNoInteractions();
    }

    @DisplayName("댓글 정보를 입력하면, 댓글을 수정한다.")
    @Test
    void insertCommentInfo_modifyComment() {
        // Given
        String originalContent = "content";
        String updateContent = "new content";
        Comment comment = createComment(originalContent);
        CommentDTO dto = createCommentDTO(updateContent);

        given(commentRepository.getReferenceById(dto.id())).willReturn(comment);

        // When
        sut.updateComment(dto);

        // Then
        assertThat(comment.getContent())
                .isNotEqualTo(originalContent)
                .isEqualTo(updateContent);
        then(commentRepository).should().getReferenceById(dto.id());
    }

    @DisplayName("없는 댓글을 수정하려고 하면, 경고 로그를 나타내고 동작하지 않는다.")
    @Test
    void noneExistComment_warningLogAndDoNothing() {
        // Given
        CommentDTO dto = createCommentDTO("댓글");
        given(commentRepository.getReferenceById(dto.id())).willThrow(EntityNotFoundException.class);

        // When
        sut.updateComment(dto);

        // Then
        then(commentRepository).should().getReferenceById(dto.id());
    }

    @DisplayName("댓글 id를 입력하면, 댓글이 삭제된다")
    @Test
    void insertCommentId_deleteComment() {
        // Given
        Long commentId = 1L;
        willDoNothing().given(commentRepository).deleteById(commentId);

        // When
        sut.deleteComment(commentId);

        // Then
        then(commentRepository).should().deleteById(commentId);
    }

    private CommentDTO createCommentDTO(String content) {
        return CommentDTO.of(1L, 1L, createUserAccountDTO(), content, LocalDateTime.now(), "mh", LocalDateTime.now(), "mh");
    }

    private UserAccountDTO createUserAccountDTO() {
        return UserAccountDTO.of("mh", "password", "test@test.test", "MH", "memo", LocalDateTime.now(),"mh", LocalDateTime.now(), "mh");
    }

    private Comment createComment(String content) {
        return Comment.of(Post.of(createUserAccount(), "title", "content", "hashtag"), createUserAccount(), content);

    }

    private UserAccount createUserAccount() {
        return UserAccount.of("mh", "pw", "test@test.test", "MH", null);
    }

    private Post createPost() {
        return Post.of(
                createUserAccount(),
                "title",
                "content",
                "#spring"
        );
    }
}