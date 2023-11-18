package com.mh.project.dto;


/**
 * DTO for {@link com.mh.project.domain.Post}
 */
public record PostUpdateDTO(
        String title,
        String content,
        String hashtag
) {

    public static PostUpdateDTO of(String title, String content, String hashtag) {
        return new PostUpdateDTO(title, content, hashtag);
    }
}