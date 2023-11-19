package com.mh.project.controller;

import com.mh.project.domain.type.SearchType;
import com.mh.project.dto.response.PostResponse;
import com.mh.project.dto.response.PostWithCommentResponse;
import com.mh.project.service.PaginationService;
import com.mh.project.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

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

        return "posts/index";
    }

    @GetMapping("/{postId}")
    public String post(@PathVariable Long postId, ModelMap map) {
        PostWithCommentResponse postWithComments = PostWithCommentResponse.from(postService.getPost(postId));
        map.addAttribute("post", postWithComments);
        map.addAttribute("comments", postWithComments.commentsResponse());
        return "posts/detail";
    }
}
