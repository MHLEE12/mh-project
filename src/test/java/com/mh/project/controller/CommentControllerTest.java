package com.mh.project.controller;

import com.mh.project.config.SecurityConfig;
import com.mh.project.dto.CommentDTO;
import com.mh.project.dto.request.CommentRequest;
import com.mh.project.service.CommentService;
import com.mh.project.util.FormDataEncoder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

@DisplayName("Comment Controller")
@Import({SecurityConfig.class, FormDataEncoder.class})
@WebMvcTest(CommentController.class)
class CommentControllerTest {

    private final MockMvc mvc;
    private final FormDataEncoder formDataEncoder;

    @MockBean
    private CommentService commentService;

    public CommentControllerTest(@Autowired MockMvc mvc, @Autowired FormDataEncoder formDataEncoder) {
        this.mvc = mvc;
        this.formDataEncoder = formDataEncoder;
    }

    @DisplayName("[view][POST] 댓글 등록 - 정상 호출")
    @Test
    void givenArticleCommentInfo_whenRequesting_thenSavesNewArticleComment() throws Exception {
        // Given
        Long postId = 1L;
        CommentRequest request = CommentRequest.of(postId, "test comment");
        willDoNothing().given(commentService).saveComment(any(CommentDTO.class));

        // When & Then
        mvc.perform(
                        post("/comments/new")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .content(formDataEncoder.encode(request))
                                .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/posts/" + postId))
                .andExpect(redirectedUrl("/posts/" + postId));
        then(commentService).should().saveComment(any(CommentDTO.class));
    }

    @DisplayName("[view][GET] 댓글 삭제 - 정상 호출")
    @Test
    void givenArticleCommentIdToDelete_whenRequesting_thenDeletesArticleComment() throws Exception {
        // Given
        Long postId = 1L;
        Long commentId = 1L;
        willDoNothing().given(commentService).deleteComment(commentId);

        // When & Then
        mvc.perform(
                        post("/comments/" + commentId + "/delete")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .content(formDataEncoder.encode(Map.of("postId", postId)))
                                .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/posts/" + postId))
                .andExpect(redirectedUrl("/posts/" + postId));
        then(commentService).should().deleteComment(commentId);
    }

}
