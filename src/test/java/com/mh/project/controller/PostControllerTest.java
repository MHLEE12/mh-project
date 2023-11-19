package com.mh.project.controller;

import com.mh.project.config.SecurityConfig;
import com.mh.project.dto.PostWithCommentDTO;
import com.mh.project.dto.UserAccountDTO;
import com.mh.project.service.PaginationService;
import com.mh.project.service.PostService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("View Controller")
@Import(SecurityConfig.class)
@WebMvcTest(PostController.class)
public class PostControllerTest {

    private final MockMvc mvc;

    @MockBean private PostService postService;
    @MockBean private PaginationService paginationService;

    public PostControllerTest(@Autowired MockMvc mvc) {
        this.mvc = mvc;
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

    @DisplayName("[view][GET] 게시글 상세 페이지 - 정상 호출")
    @Test
    void show_postDetailPage() throws Exception {
        // Given
        Long postId = 1L;
        given(postService.getPost(postId)).willReturn(createPostWithCommentDTO());

        // When & Then
        mvc.perform(get("/posts/" + postId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("posts/detail"))
                .andExpect(model().attributeExists("post"))
                .andExpect(model().attributeExists("comments"));
        then(postService).should().getPost(postId);
    }

    @Disabled("구현 중")
    @DisplayName("[view][GET] 게시글 해시태그 검색 페이지 - 정상 호출")
    @Test
    void show_searchPage() throws Exception {
        // Given

        // When & Then
        mvc.perform(get("/posts/search"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("posts/search"));
    }

    @Disabled("구현 중")
    @DisplayName("[view][GET] 게시글 해시태그 검색 페이지 - 정상 호출")
    @Test
    void show_searchHashtagPage() throws Exception {
        // Given

        // When & Then
        mvc.perform(get("/posts/search-hashtag"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("posts/search-hashtag"));
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
