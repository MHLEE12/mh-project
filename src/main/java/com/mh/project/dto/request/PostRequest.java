package com.mh.project.dto.request;

import com.mh.project.domain.Post;
import com.mh.project.domain.UserAccount;
import com.mh.project.dto.PostDTO;
import com.mh.project.dto.UserAccountDTO;

public record PostRequest(
        String title,
        String content,
        String hashtag
) {

    public static PostRequest of(String title, String content, String hashtag) {
        return new PostRequest(title, content, hashtag);
    }

    public PostDTO toDTO(UserAccountDTO userAccountDTO) {
        return PostDTO.of(
                userAccountDTO,
                title,
                content,
                hashtag
        );
    }


}
