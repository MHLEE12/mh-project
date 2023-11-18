package com.mh.project.service;

import com.mh.project.domain.Post;
import com.mh.project.domain.type.SearchType;
import com.mh.project.dto.PostDTO;
import com.mh.project.dto.PostUpdateDTO;
import com.mh.project.dto.PostWithCommentDTO;
import com.mh.project.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class PostService {

    private final PostRepository postRepository;

    @Transactional(readOnly = true)
    public Page<PostDTO> searchPosts(SearchType searchType, String searchKeyword, Pageable pageable) {
        if (searchKeyword == null || searchKeyword.isBlank()) {
            return postRepository.findAll(pageable).map(PostDTO::from);
        }

        return switch (searchType) {
            case TITLE -> postRepository.findByTitleContaining(searchKeyword, pageable).map(PostDTO::from);
            case CONTENT -> postRepository.findByContentContaining(searchKeyword, pageable).map(PostDTO::from);
            case ID -> postRepository.findByUserAccount_UserIdContaining(searchKeyword, pageable).map(PostDTO::from);
            case NICKNAME -> postRepository.findByUserAccount_NicknameContaining(searchKeyword, pageable).map(PostDTO::from);
            case HASHTAG -> postRepository.findByHashtag("#" + searchKeyword, pageable).map(PostDTO::from);
        };
    }

    @Transactional(readOnly = true)
    public PostWithCommentDTO getPost(Long postId) {
        return postRepository.findById(postId)
                .map(PostWithCommentDTO::from)
                .orElseThrow(() -> new EntityNotFoundException("게시글이 없습니다. - postId: " + postId));
    }

    public void savePost(PostDTO dto) {
        postRepository.save(dto.toEntity());
    }

    public void updatePost(PostDTO dto) {
        try {
            Post post = postRepository.getReferenceById(dto.id());
            if (dto.title() != null) { post.setTitle(dto.title()); }
            if (dto.content() != null) { post.setContent(dto.content()); }
            post.setHashtag(dto.hashtag());
        } catch (EntityNotFoundException e) {
            log.warn("게시글 수정에 실패했습니다. 게시글을 찾을 수 없습니다. - dto: {}", dto);
        }
    }

    public void deletePost(long postId) {
        postRepository.deleteById(postId);
    }
}
