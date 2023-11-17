package com.mh.project.service;

import com.mh.project.domain.type.SearchType;
import com.mh.project.dto.PostDTO;
import com.mh.project.dto.PostUpdateDTO;
import com.mh.project.dto.PostWithCommentDTO;
import com.mh.project.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
public class PostService {

    private final PostRepository postRepository;

    @Transactional(readOnly = true)
    public Page<PostDTO> searchPosts(SearchType searchType, String searchKeyword, Pageable pageable) {
        return Page.empty();
    }

    @Transactional(readOnly = true)
    public PostDTO selectPost(Long postId) {
        return null;
    }

    public void savePost(PostDTO dto) {

    }

    public void updatePost(long postId, PostUpdateDTO dto) {

    }

    public void deletePost(long postId) {
    }

    public PostWithCommentDTO getPost(Long postId) {
        return null;
    }

    public void getArticle(Long noneExistPostId) {
    }

    public void updatePost(PostDTO dto) {
    }
}
