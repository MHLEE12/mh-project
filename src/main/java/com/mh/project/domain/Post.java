package com.mh.project.domain;

import java.time.LocalDateTime;

public class Post {
    private Long postId;            // 게시글id
    private String title;           // 제목
    private String content;         // 내용
    private String hashtag;         // 해시태그

    private LocalDateTime creDate;  // 생성일자
    private String creUser;         // 생성자
    private LocalDateTime modDate;  // 수정일자
    private String modUser;         // 수정자
}
