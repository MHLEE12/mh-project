package com.mh.project.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@ToString
@Table(indexes = { // 빠른 서치가 가능하도록 인덱스를 걺
        @Index(columnList = "title"),
        @Index(columnList = "hashtag"),
        @Index(columnList = "creDate"),
        @Index(columnList = "creUser")
})
@EntityListeners(AuditingEntityListener.class)
@Entity
public class Post {
    // postId, creUser, modUser등을 마음대로 바꾸지 못하게 전체 setter를 걸지 않고 일부만 setter를 걺

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;            // 게시글id


    @Setter
    @Column(nullable = false)
    private String title;           // 제목

    @Setter
    @Column(nullable = false, length = 10000)
    private String content;         // 내용

    @Setter
    private String hashtag;         // 해시태그

    @ToString.Exclude
    @OrderBy("id")
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private final Set<Comment> comment = new LinkedHashSet<>();

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @CreatedDate
    @Column(nullable = false, updatable = false) // updatable : 수정 불가하도록 함.
    private LocalDateTime creDate;  // 생성일자

    @CreatedBy
    @Column(nullable = false, updatable = false, length = 100)
    private String creUser;         // 생성자

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime modDate;  // 수정일자

    @LastModifiedBy
    @Column(nullable = false, length = 100)
    private String modUser;         // 수정자

    protected Post() {}

    public Post(String title, String content, String hashtag) {
        this.title = title;
        this.content = content;
        this.hashtag = hashtag;
    }

    public static Post of(String title, String content, String hashtag) {
        return new Post(title, content, hashtag);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Post post)) return false;
        return id != null && id.equals(post.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
