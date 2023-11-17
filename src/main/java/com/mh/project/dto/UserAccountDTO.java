package com.mh.project.dto;

import com.mh.project.domain.UserAccount;

import java.time.LocalDateTime;

/**
 * DTO for {@link com.mh.project.domain.UserAccount}
 */
public record UserAccountDTO(
        Long id,
        String userId,
        String userPassword,
        String email,
        String nickname,
        String memo,
        LocalDateTime creDate,
        String creUser,
        LocalDateTime modDate,
        String modUser
) {

    public static UserAccountDTO of(Long id, String userId, String userPassword, String email, String nickname, String memo, LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt, String modifiedBy) {
        return new UserAccountDTO(id, userId, userPassword, email, nickname, memo, createdAt, createdBy, modifiedAt, modifiedBy);
    }

    public static UserAccountDTO from(UserAccount entity) {
        return new UserAccountDTO(
                entity.getId(),
                entity.getUserId(),
                entity.getUserPassword(),
                entity.getEmail(),
                entity.getNickname(),
                entity.getMemo(),
                entity.getCreDate(),
                entity.getCreUser(),
                entity.getModDate(),
                entity.getModUser()
        );
    }

    public UserAccount toEntity() {
        return UserAccount.of(
                userId,
                userPassword,
                email,
                nickname,
                memo
        );
    }
}