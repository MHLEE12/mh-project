package com.mh.project.controller;

import com.mh.project.dto.UserAccountDTO;
import com.mh.project.dto.request.CommentRequest;
import com.mh.project.dto.security.BoardPrincipal;
import com.mh.project.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public String postingNewComment(
            @AuthenticationPrincipal BoardPrincipal boardPrincipal,
            CommentRequest commentRequest
    ) {
        commentService.saveComment(commentRequest.toDTO(boardPrincipal.toDTO()));

        return "redirect:/posts/" + commentRequest.postId() ;
    }

    @PostMapping("/{commentId}/delete")
    public String deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal BoardPrincipal boardPrincipal,
            Long postId
    ) {
        commentService.deleteComment(commentId, boardPrincipal.getUsername());

        return "redirect:/posts/" + postId;
    }
}
