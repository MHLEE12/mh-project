package com.mh.project.domain;

import java.time.LocalDateTime;

public class Comment {
    private Long commentId;         // 댓글id
    private Post post;              // 게시글id
    private String content;         // 댓글 내용

    private LocalDateTime creDate;  // 생성일자
    private String creUser;         // 생성자
    private LocalDateTime modDate;  // 수정일자
    private String modUser;         // 수정자
}
