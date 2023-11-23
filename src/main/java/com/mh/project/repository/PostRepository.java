package com.mh.project.repository;

import com.mh.project.domain.Post;
import com.mh.project.domain.QPost;
import com.mh.project.repository.querydsl.PostRepositoryCustom;
import com.querydsl.core.types.dsl.DateTimeExpression;
import com.querydsl.core.types.dsl.StringExpression;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface PostRepository extends
        JpaRepository<Post, Long>,
        PostRepositoryCustom,
        QuerydslPredicateExecutor<Post>,
        QuerydslBinderCustomizer<QPost> {

    Page<Post> findByTitleContaining(String title, Pageable pageable);
    Page<Post> findByContentContaining(String content, Pageable pageable);
    Page<Post> findByUserAccount_UserIdContaining(String userId, Pageable pageable);
    Page<Post> findByUserAccount_NicknameContaining(String nickname, Pageable pageable);
    Page<Post> findByHashtag(String hashtag, Pageable pageable);

    void deleteByIdAndUserAccount_UserId(Long postId, String userId);

    @Override
    default void customize(QuerydslBindings bindings, QPost root) {
        bindings.excludeUnlistedProperties(true);
        bindings.including(root.title, root.content, root.hashtag, root.creDate, root.creUser);
        bindings.bind(root.title).first(StringExpression::containsIgnoreCase);
        bindings.bind(root.hashtag).first(StringExpression::containsIgnoreCase);
        bindings.bind(root.creDate).first(DateTimeExpression::eq);
        bindings.bind(root.creUser).first(StringExpression::containsIgnoreCase);
    }

}
