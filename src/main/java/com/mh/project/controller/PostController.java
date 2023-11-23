package com.mh.project.controller;

import com.mh.project.domain.constant.FormStatus;
import com.mh.project.domain.type.SearchType;
import com.mh.project.dto.UserAccountDTO;
import com.mh.project.dto.request.PostRequest;
import com.mh.project.dto.response.PostResponse;
import com.mh.project.dto.response.PostWithCommentResponse;
import com.mh.project.dto.security.BoardPrincipal;
import com.mh.project.service.PaginationService;
import com.mh.project.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RequestMapping("/posts")
@Controller
public class PostController {

    private final PostService postService;
    private final PaginationService paginationService;

    @GetMapping
    public String posts(
            @RequestParam(required = false) SearchType searchType,
            @RequestParam(required = false) String searchValue,
            @PageableDefault(size = 10, sort = "creDate", direction = Sort.Direction.DESC) Pageable pageable,
            ModelMap map
    ) {
        Page<PostResponse> postResponse = postService.searchPosts(searchType, searchValue, pageable).map(PostResponse::from);
        List<Integer> barNum = paginationService.getPaginationBarNumbers(pageable.getPageNumber(), postResponse.getTotalPages());

        map.addAttribute("posts", postResponse);
        map.addAttribute("paginationBarNumbers", barNum);
        map.addAttribute("searchTypes", SearchType.values());

        return "posts/index";
    }

    @GetMapping("/{postId}")
    public String post(@PathVariable Long postId, ModelMap map) {
        PostWithCommentResponse postWithComments = PostWithCommentResponse.from(postService.getPostWithComments(postId));

        map.addAttribute("totalPages", paginationService.currentPostSize());
        map.addAttribute("post", postWithComments);
        map.addAttribute("comments", postWithComments.commentsResponse());
        map.addAttribute("totalCount", postService.getPostCount());

        return "posts/detail";
    }

    @GetMapping("/search-hashtag")
    public String searchHashtag(
            @RequestParam(required = false) String searchValue,
            @PageableDefault(size = 10, sort = "creDate", direction = Sort.Direction.DESC) Pageable pageable,
            ModelMap map
    ) {
        Page<PostResponse> postResponse = postService.searchPostsViaHashtag(searchValue, pageable).map(PostResponse::from);
        List<Integer> barNum = paginationService.getPaginationBarNumbers(pageable.getPageNumber(), postResponse.getTotalPages());
        List<String> hashtags = postService.getHashtags();

        map.addAttribute("posts", postResponse);
        map.addAttribute("hashtags", hashtags);
        map.addAttribute("paginationBarNumbers", barNum);
        map.addAttribute("searchType", SearchType.HASHTAG);

        return "posts/search-hashtag";
    }

    @GetMapping("/form")
    public String postForm(ModelMap map) {
        map.addAttribute("formStatus", FormStatus.CREATE);

        return "posts/form";
    }

    @PostMapping("/form")
    public String postingNewPost(
            PostRequest postRequest,
            @AuthenticationPrincipal BoardPrincipal boardPrincipal
    ) {
        postService.savePost(postRequest.toDTO(boardPrincipal.toDTO()));

        return "redirect:/posts";
    }

    @GetMapping("/{postId}/form")
    public String updatePost(@PathVariable Long postId, ModelMap map) {
        PostResponse post = PostResponse.from(postService.getPost(postId));

        map.addAttribute("post", post);
        map.addAttribute("formStatus", FormStatus.UPDATE);

        return "posts/form";
    }

    @PostMapping ("/{postId}/form")
    public String updatePost(
            @PathVariable Long postId,
            PostRequest postRequest,
            @AuthenticationPrincipal BoardPrincipal boardPrincipal
    ) {
        postService.updatePost(postId, postRequest.toDTO(boardPrincipal.toDTO()));

        return "redirect:/posts/" + postId;
    }

    @PostMapping ("/{postId}/delete")
    public String deletePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal BoardPrincipal boardPrincipal
            ) {
        postService.deletePost(postId, boardPrincipal.getUsername());

        return "redirect:/posts";
    }
}
