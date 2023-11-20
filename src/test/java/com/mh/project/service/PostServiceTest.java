package com.mh.project.service;

import com.mh.project.domain.Post;
import com.mh.project.domain.UserAccount;
import com.mh.project.domain.type.SearchType;
import com.mh.project.dto.PostDTO;
import com.mh.project.dto.PostWithCommentDTO;
import com.mh.project.dto.UserAccountDTO;
import com.mh.project.repository.PostRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@DisplayName("비즈니스 로직 - 게시판")
@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @InjectMocks private PostService sut; // system under test
    @Mock private PostRepository postRepository;

    @DisplayName("검색어 없이 게시글을 검색하면, 게시글 페이지를 반환한다.")
    @Test
    void noSearchParam_showPostPage() {
        // Given
        Pageable pageable = Pageable.ofSize(20);
        given(postRepository.findAll(pageable)).willReturn(Page.empty());

        // When
        Page<PostDTO> posts = sut.searchPosts(null, null, pageable);

        // Then
        assertThat(posts).isEmpty();
        then(postRepository).should().findAll(pageable);
    }

    @DisplayName("검색어 없이 해시태그 검색창에서 검색하면, 게시글 페이지를 반환한다.")
    @Test
    void noSearchParam_showHashtagsearchPage() {
        // Given
        Pageable pageable = Pageable.ofSize(20);


        // When
        Page<PostDTO> posts = sut.searchPostsViaHashtag(null, pageable);

        // Then
        assertThat(posts).isEqualTo(Page.empty(pageable));
        then(postRepository).shouldHaveNoInteractions();
    }

    @DisplayName("검색어를 해시태그 검색창에 입력하면, 게시글 페이지를 반환한다.")
    @Test
    void insertSearchParam_showHashtagsearchPage() {
        // Given
        String hashtag = "#blue";
        Pageable pageable = Pageable.ofSize(20);
        given(postRepository.findByHashtag(hashtag, pageable)).willReturn(Page.empty(pageable));

        // When
        Page<PostDTO> posts = sut.searchPostsViaHashtag(hashtag, pageable);

        // Then
        assertThat(posts).isEqualTo(Page.empty(pageable));
        then(postRepository).should().findByHashtag(hashtag, pageable);
    }

    @DisplayName("검색어를 해시태그 검색창에 입력하면, 해시태그 리스트를 보여준다.")
    @Test
    void insertHashtag_showHashtagList() {
        // Given
        List<String> expect = List.of("#java", "#spring", "#springboot");
        given(postRepository.findAllDistinctHashtags()).willReturn(expect);

        // When
        List<String> actual = sut.getHashtags();

        // Then
        assertThat(actual).isEqualTo(expect);
        then(postRepository).should().findAllDistinctHashtags();
    }

    @DisplayName("검색어와 함께 게시글을 검색하면, 게시글 페이지를 반환한다.")
    @Test
    void searchParameter_showPostList() {
        // Given
        SearchType searchType = SearchType.TITLE;
        String searchKeyword = "title";
        Pageable pageable = Pageable.ofSize(20);
        given(postRepository.findByTitleContaining(searchKeyword, pageable)).willReturn(Page.empty());

        // When
        Page<PostDTO> posts = sut.searchPosts(searchType, searchKeyword, pageable);

        // Then
        assertThat(posts).isEmpty();
        then(postRepository).should().findByTitleContaining(searchKeyword, pageable);
    }

    @DisplayName("게시글 조회하면 해당 게시글 리스트를 반환한다.")
    @Test
    void searchPostsList() {
        // Given
        Long postId = 1L;
        Post post = createPost();
        given(postRepository.findById(postId)).willReturn(Optional.of(post));

        // When
        PostWithCommentDTO dto = sut.getPost(postId);

        // Then
        assertThat(dto)
                .hasFieldOrPropertyWithValue("title", post.getTitle())
                .hasFieldOrPropertyWithValue("content", post.getContent())
                .hasFieldOrPropertyWithValue("hashtag", post.getHashtag());
        then(postRepository).should().findById(postId);
    }

    @DisplayName("존재하지 않는 게시글을 조회하면, 예외처리 된다.")
    @Test
    void noneExistPostSelect_throwException() {
        // Given
        Long noneExistPostId = 100L;
        given(postRepository.findById(noneExistPostId)).willReturn(Optional.empty());

        // When
        Throwable t = catchThrowable(() -> sut.getPost(noneExistPostId));

        // Then
        assertThat(t)
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("게시글이 없습니다. - postId: " + noneExistPostId);
        then(postRepository).should().findById(noneExistPostId);
    }

    @DisplayName("게시글을 등록하면 게시글이 생성된다.")
    @Test
    void insertInfo_savePost() {
        // Given
        PostDTO dto = createPostDTO();
        given(postRepository.save(any(Post.class))).willReturn(createPost());

        // When
        sut.savePost(dto);

        // Then
        then(postRepository).should().save(any(Post.class));
    }

    @DisplayName("게시글의 수정 정보를 입력하면, 게시글을 수정한다.")
    @Test
    void insertModiInfo_updatePost() {
        // Given
        Post post = createPost();
        PostDTO dto = createPostDTO("제목 수정", "내용 수정", "#spring");
        given(postRepository.getReferenceById(dto.id())).willReturn(post);

        // When
        sut.updatePost(dto);

        // Then
        assertThat(post)
                .hasFieldOrPropertyWithValue("title", dto.title())
                .hasFieldOrPropertyWithValue("content", dto.content())
                .hasFieldOrPropertyWithValue("hashtag", dto.hashtag());
        then(postRepository).should().getReferenceById(dto.id());
    }

    @DisplayName("존재하지 않는 게시글의 수정 정보를 입력하면, 경고 로그가 찍히고, 아무런 작동을 하지 않는다.")
    @Test
    void noneExistPostUpdate_warningLogAndDoNothing() {
        // Given
        PostDTO dto = createPostDTO("제목 수정", "내용 수정", "#수정");
        given(postRepository.getReferenceById(dto.id())).willThrow(EntityNotFoundException.class);

        // When
        sut.updatePost(dto);

        // Then
        then(postRepository).should().getReferenceById(dto.id());
    }

    @DisplayName("게시글의 id를 입력하면, 게시글을 삭제한다.")
    @Test
    void insertId_deletePost() {
        // Given
        Long postId = 1L;
        willDoNothing().given(postRepository).deleteById(postId);

        // When
        sut.deletePost(1L);

        // Then
        then(postRepository).should().deleteById(postId);
    }

    private UserAccount createUserAccount() {
        return UserAccount.of(
                "mh",
                "pw",
                "test@test.test",
                "MH",
                null
        );
    }

    private Post createPost() {
        return Post.of(
                createUserAccount(),
                "title",
                "content",
                "#hashtag"
        );
    }

    private PostDTO createPostDTO() {
        return createPostDTO("title", "content", "#hashtag");
    }

    private PostDTO createPostDTO(String title, String content, String hashtag) {
        return PostDTO.of(
                1L,
                createUserAccountDTO(),
                title,
                content,
                hashtag,
                LocalDateTime.now(),
                "mh",
                LocalDateTime.now(),
                "mh"
        );
    }

    private UserAccountDTO createUserAccountDTO() {
        return UserAccountDTO.of(
                1L,
                "mh",
                "pw",
                "test@test.test",
                "MH",
                "memo",
                LocalDateTime.now(),
                "mh",
                LocalDateTime.now(),
                "mh"
        );
    }
}