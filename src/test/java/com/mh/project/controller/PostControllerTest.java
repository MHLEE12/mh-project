package com.mh.project.controller;

import com.mh.project.domain.constant.FormStatus;
import com.mh.project.domain.type.SearchType;
import com.mh.project.dto.PostDTO;
import com.mh.project.dto.PostWithCommentDTO;
import com.mh.project.dto.UserAccountDTO;
import com.mh.project.dto.request.PostRequest;
import com.mh.project.dto.response.PostResponse;
import com.mh.project.service.PaginationService;
import com.mh.project.service.PostService;
import com.mh.project.util.FormDataEncoder;
import com.mh.project.util.TestSecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("View Controller")
@Import({TestSecurityConfig.class, FormDataEncoder.class})
@WebMvcTest(PostController.class)
public class PostControllerTest {

    private final MockMvc mvc;
    private final FormDataEncoder formDataEncoder;

    @MockBean private PostService postService;
    @MockBean private PaginationService paginationService;

    public PostControllerTest(@Autowired MockMvc mvc, @Autowired FormDataEncoder formDataEncoder) {
        this.mvc = mvc;
        this.formDataEncoder = formDataEncoder;
    }

    @DisplayName("[view][GET] 게시글 리스트 페이지 - 정상 호출")
    @Test
    void show_postListPage() throws Exception {
        // Given
        given(postService.searchPosts(eq(null), eq(null), any(Pageable.class))).willReturn(Page.empty());
        given(paginationService.getPaginationBarNumbers(anyInt(), anyInt())).willReturn(List.of(0, 1, 2, 3, 4));

        // When & Then
        mvc.perform(get("/posts"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("posts/index"))
                .andExpect(model().attributeExists("posts"))
                .andExpect(model().attributeExists("paginationBarNumbers"));
        then(postService).should().searchPosts(eq(null), eq(null), any(Pageable.class));
        then(paginationService).should().getPaginationBarNumbers(anyInt(), anyInt());
    }

    @DisplayName("[view][GET] 검색어가 들어간 게시글 리스트 페이지 - 검색어 게시글 정상 호출")
    @Test
    void insertSearchkeyword_showpostListPage() throws Exception {
        // Given
        SearchType searchType = SearchType.TITLE;
        String searchValue = "title";
        given(postService.searchPosts(eq(searchType), eq(searchValue), any(Pageable.class))).willReturn(Page.empty());
        given(paginationService.getPaginationBarNumbers(anyInt(), anyInt())).willReturn(List.of(0, 1, 2, 3, 4));

        // When & Then
        mvc.perform(get("/posts")
                        .queryParam("searchType", searchType.name())
                        .queryParam("searchValue", searchValue))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("posts/index"))
                .andExpect(model().attributeExists("posts"))
                .andExpect(model().attributeExists("searchTypes"));
        then(postService).should().searchPosts(eq(searchType), eq(searchValue), any(Pageable.class));
        then(paginationService).should().getPaginationBarNumbers(anyInt(), anyInt());
    }

    @DisplayName("[view][GET] 게시글 리스트 페이지 - 페이징, 정렬 기능")
    @Test
    void givenPagingAndSortInfo_showPostList() throws Exception {
        // Given
        String sortName = "title";
        String direction = "desc";
        int pageNum = 0;
        int pageSize = 5;
        Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.by(Sort.Order.desc(sortName)));
        List<Integer> barNumbers = List.of(1, 2, 3, 4, 5);
        given(postService.searchPosts(null, null, pageable)).willReturn(Page.empty());
        given(paginationService.getPaginationBarNumbers(pageable.getPageNumber(), Page.empty().getTotalPages())).willReturn(barNumbers);

        // When & Then
        mvc.perform(
                        get("/posts")
                                .queryParam("page", String.valueOf(pageNum))
                                .queryParam("size", String.valueOf(pageSize))
                                .queryParam("sort", sortName + "," + direction)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("posts/index"))
                .andExpect(model().attributeExists("posts"))
                .andExpect(model().attribute("paginationBarNumbers", barNumbers));
        then(postService).should().searchPosts(null, null, pageable);
        then(paginationService).should().getPaginationBarNumbers(pageable.getPageNumber(), Page.empty().getTotalPages());
    }

    @DisplayName("[view][GET] 게시글 상세 페이지 - 인증 안된 사용자는 로그인 페이지로 이동")
    @Test
    void noAuth_goLoginPage() throws Exception {
        // Given
        Long postId = 1L;

        // When & Then
        mvc.perform(get("/posts/" + postId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
        then(postService).shouldHaveNoInteractions();
    }

    @WithMockUser
    @DisplayName("[view][GET] 게시글 상세 페이지 - 정상 호출, 인증된 사용자")
    @Test
    void show_postDetailPage() throws Exception {
        // Given
        Long postId = 1L;
        Long totalCount = 1L;
        given(postService.getPostWithComments(postId)).willReturn(createPostWithCommentDTO());
        given(postService.getPostCount()).willReturn(totalCount);

        // When & Then
        mvc.perform(get("/posts/" + postId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("posts/detail"))
                .andExpect(model().attributeExists("post"))
                .andExpect(model().attributeExists("comments"))
                .andExpect(model().attribute("totalCount", totalCount));
        then(postService).should().getPostWithComments(postId);
        then(postService).should().getPostCount();
    }

    @DisplayName("[view][GET] 게시글 해시태그 검색 페이지 - 검색어 입력X, 정상 호출")
    @Test
    void noneSearchValue_showsearchPage() throws Exception {
        // Given
        List<String> hashtags = List.of("#java", "#spring", "#winter");
        given(postService.searchPostsViaHashtag(eq(null), any(Pageable.class))).willReturn(Page.empty());
        given(postService.getHashtags()).willReturn(hashtags);
        given(paginationService.getPaginationBarNumbers(anyInt(), anyInt())).willReturn(List.of(1,2,3,4,5));

        mvc.perform(get("/posts/search-hashtag"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("posts/search-hashtag"))
                .andExpect(model().attribute("hashtags", hashtags))
                .andExpect(model().attributeExists("paginationBarNumbers"))
                .andExpect(model().attribute("searchType", SearchType.HASHTAG));
        // When & Then
        then(postService).should().searchPostsViaHashtag(eq(null), any(Pageable.class));
        then(postService).should().getHashtags();
        then(paginationService).should().getPaginationBarNumbers(anyInt(), anyInt());
    }

    @DisplayName("[view][GET] 게시글 해시태그 검색 페이지 - 검색어 입력O, 정상 호출")
    @Test
    void insertSearchValue_showsearchPage() throws Exception {
        // Given
        String hashtag = "#java";
        List<String> hashtags = List.of("#java", "#spring", "#winter");
        given(postService.searchPostsViaHashtag(eq(hashtag), any(Pageable.class))).willReturn(Page.empty());
        given(paginationService.getPaginationBarNumbers(anyInt(), anyInt())).willReturn(List.of(1,2,3,4,5));
        given(postService.getHashtags()).willReturn(hashtags);

        // When & Then
        mvc.perform(get("/posts/search-hashtag")
                        .queryParam("searchValue", hashtag))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("posts/search-hashtag"))
                .andExpect(model().attribute("hashtags", hashtags))
                .andExpect(model().attributeExists("paginationBarNumbers"))
                .andExpect(model().attribute("searchType", SearchType.HASHTAG));
        then(postService).should().searchPostsViaHashtag(eq(hashtag), any(Pageable.class));
        then(postService).should().getHashtags();
        then(paginationService).should().getPaginationBarNumbers(anyInt(), anyInt());
    }

    @WithMockUser
    @DisplayName("[view][GET] 새 게시글 작성 페이지")
    @Test
    void insertNothing_showNewPost() throws Exception {
        // Given

        // When & Then
        mvc.perform(get("/posts/form"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("posts/form"))
                .andExpect(model().attribute("formStatus", FormStatus.CREATE));
    }

    @WithUserDetails(value = "mhTest", userDetailsServiceBeanName = "userDetailService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[view][POST] 새 게시글 등록 - 정상 호출")
    @Test
    void givenNewpostInfo_whenRequesting_thenSavesNewpost() throws Exception {
        // Given
        PostRequest postRequest = PostRequest.of("new title", "new content", "#new");
        willDoNothing().given(postService).savePost(any(PostDTO.class));

        // When & Then
        mvc.perform(
                        post("/posts/form")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .content(formDataEncoder.encode(postRequest))
                                .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/posts"))
                .andExpect(redirectedUrl("/posts"));
        then(postService).should().savePost(any(PostDTO.class));
    }

    @WithMockUser
    @DisplayName("[view][GET] 게시글 수정 페이지 - 정상 호출, 인증된 사용자")
    @Test
    void givenNothing_whenRequesting_thenReturnsUpdatedpostPage() throws Exception {
        // Given
        long postId = 1L;
        PostDTO dto = createPostDTO();
        given(postService.getPost(postId)).willReturn(dto);

        // When & Then
        mvc.perform(get("/posts/" + postId + "/form"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("posts/form"))
                .andExpect(model().attribute("post", PostResponse.from(dto)))
                .andExpect(model().attribute("formStatus", FormStatus.UPDATE));
        then(postService).should().getPost(postId);
    }

    @WithUserDetails(value = "mhTest", userDetailsServiceBeanName = "userDetailService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[view][POST] 게시글 수정 - 정상 호출")
    @Test
    void givenUpdatedpostInfo_whenRequesting_thenUpdatesNewpost() throws Exception {
        // Given
        Long postId = 1L;
        PostRequest postRequest = PostRequest.of("new title", "new content", "#new");
        willDoNothing().given(postService).updatePost(eq(postId), any(PostDTO.class));

        // When & Then
        mvc.perform(
                        post("/posts/" + postId + "/form")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .content(formDataEncoder.encode(postRequest))
                                .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/posts/" + postId))
                .andExpect(redirectedUrl("/posts/" + postId));
        then(postService).should().updatePost(eq(postId), any(PostDTO.class));
    }

    @WithUserDetails(value = "mhTest", userDetailsServiceBeanName = "userDetailService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[view][POST] 게시글 삭제 - 정상 호출")
    @Test
    void givenpostIdToDelete_whenRequesting_thenDeletesPost() throws Exception {
        // Given
        Long postId = 1L;
        String userId = "mhTest";
        willDoNothing().given(postService).deletePost(postId, userId);

        // When & Then
        mvc.perform(
                        post("/posts/" + postId + "/delete")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/posts"))
                .andExpect(redirectedUrl("/posts"));
        then(postService).should().deletePost(postId, userId);
    }


    private PostDTO createPostDTO() {
        return PostDTO.of(
                createUserAccountDTO(),
                "title",
                "content",
                "#java"
        );
    }

    private PostWithCommentDTO createPostWithCommentDTO() {
        return PostWithCommentDTO.of(
                1L,
                createUserAccountDTO(),
                Set.of(),
                "title",
                "content",
                "#spring",
                LocalDateTime.now(),
                "mh",
                LocalDateTime.now(),
                "mh"
        );
    }

    private UserAccountDTO createUserAccountDTO() {
        return UserAccountDTO.of(
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
