package com.mh.project.repository;

import com.mh.project.config.JpaConfig;
import com.mh.project.domain.Post;
import com.mh.project.domain.UserAccount;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Disabled("Spring Data REST 통합 테스트는 불필요하므로 제외함")
@DisplayName("JPA 연결 테스트")
@Import(JpaConfig.class)
@DataJpaTest
class JpaRepositoryTest {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserAccountRepository userAccountRepository;


    JpaRepositoryTest(
            @Autowired PostRepository postRepository,
            @Autowired CommentRepository commentRepository,
            @Autowired UserAccountRepository userAccountRepository) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.userAccountRepository = userAccountRepository;
    }

    @DisplayName("jpa select test")
    @Test
    void selectTest() {
        // Given

        // When
        List<Post> posts = postRepository.findAll();

        // Then
        assertThat(posts)
                .isNotNull()
                .hasSize(30);
    }

    @DisplayName("jpa insert test")
    @Test
    void insertTest() {
        // Given
        long prePostCount = postRepository.count();
        UserAccount userAccount = userAccountRepository.save(UserAccount.of("mh", "pw", null, null, null));
        Post post = Post.of(userAccount, "title", "content", "#spring");

        // When
        postRepository.save(post);

        // Then
        assertThat(postRepository.count()).isEqualTo(prePostCount + 1);
    }

    @DisplayName("jpa update test")
    @Test
    void updateTest() {
        // Given
        Post onePost = postRepository.findById(1L).orElseThrow();
        String updateHashtag = "#spring";
        onePost.setHashtag(updateHashtag);

        // When
        Post savedPost = postRepository.saveAndFlush(onePost);

        // Then
        assertThat(savedPost).hasFieldOrPropertyWithValue("hashtag", updateHashtag);
    }

    @DisplayName("jpa delete test")
    @Test
    void deleteTest() {
        // Given
        Post onePost = postRepository.findById(1L).orElseThrow();
        long prePostCount = postRepository.count();
        long preCommentCount = commentRepository.count();
        int deletedCommentSize = onePost.getComment().size();

        // When
        postRepository.delete(onePost);

        // Then
        assertThat(postRepository.count()).isEqualTo(prePostCount - 1);
        assertThat(commentRepository.count()).isEqualTo(preCommentCount - deletedCommentSize);
    }
}
