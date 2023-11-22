package com.mh.project.controller;

import com.mh.project.dto.UserAccountDTO;
import com.mh.project.dto.request.CommentRequest;
import com.mh.project.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@RequestMapping("/comments")
@Controller
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/new")
    public String postingNewComment(CommentRequest commentRequest) {
        // TODO: 인증 정보를 넣어야 함.
        commentService.saveComment(commentRequest.toDTO(UserAccountDTO.of(
                "mh1",
                "a123",
                "test@test.test",
                null,
                null
        )));

        return "redirect:/posts/" + commentRequest.postId() ;
    }

    @PostMapping("/{commentId}/delete")
    public String deleteComment(@PathVariable Long commentId, Long postId) {
        commentService.deleteComment(commentId);

        return "redirect:/posts/" + postId;
    }
}
