package com.mh.project.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@ToString(callSuper = true)
@Table(indexes = { // 빠른 서치가 가능하도록 인덱스를 걺
        @Index(columnList = "content"),
        @Index(columnList = "creDate"),
        @Index(columnList = "creUser")
})
@EntityListeners(AuditingEntityListener.class)
@Entity
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;         // 댓글id


    @Setter
    @ManyToOne(optional = false)
    private Post post;              // 게시글id

    @Setter
    @ManyToOne(optional = false)
    @JoinColumn(name = "userId")
    private UserAccount userAccount;

    @Setter
    @Column(nullable = false, length = 500)
    private String content;         // 댓글 내용


    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @CreatedDate
    @Column(nullable = false, updatable = false)
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

    protected Comment() {}

    private Comment(Post post, UserAccount userAccount, String content) {
        this.post = post;
        this.userAccount = userAccount;
        this.content = content;
    }

    public static Comment of(Post post, UserAccount userAccount, String content) {
        return new Comment(post, userAccount, content);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Comment that)) return false;
        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
