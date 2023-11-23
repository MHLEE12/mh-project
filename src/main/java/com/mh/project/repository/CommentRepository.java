package com.mh.project.repository;

import com.mh.project.domain.Comment;
import com.mh.project.domain.QComment;
import com.querydsl.core.types.dsl.DateTimeExpression;
import com.querydsl.core.types.dsl.StringExpression;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
public interface CommentRepository extends
        JpaRepository<Comment, Long>,
        QuerydslPredicateExecutor<Comment>,
        QuerydslBinderCustomizer<QComment> {

    List<Comment> findByPostId(Long postId);
    void deleteByIdAndUserAccount_UserId(Long commentId, String userId);

    @Override
    default void customize(QuerydslBindings bindings, QComment root) {
        bindings.excludeUnlistedProperties(true);
        bindings.including(root.content, root.creDate, root.creUser);
        bindings.bind(root.creDate).first(DateTimeExpression::eq);
        bindings.bind(root.creUser).first(StringExpression::containsIgnoreCase);
    }
}
