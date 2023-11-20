package com.mh.project.repository.querydsl;

import com.mh.project.domain.Post;
import com.mh.project.domain.QPost;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

public class PostRepositoryCustomImpl extends QuerydslRepositorySupport implements PostRepositoryCustom {

    public PostRepositoryCustomImpl() {
        super(Post.class);
    }

    @Override
    public List<String> findAllDistinctHashtags() {
        QPost post = QPost.post;

        JPQLQuery<String> query = from(post)
                .distinct()
                .select(post.hashtag)
                .where(post.hashtag.isNotNull());

        return query.fetch();
    }
}
