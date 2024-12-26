package com.leedae.boardandadmin.dto;


import com.leedae.boardandadmin.domain.constant.RoleType;

import java.time.LocalDateTime;
import java.util.Set;

public record UserAccountDto(
        String userId,
        Set<RoleType> roleTypes,
        String email,
        String nickname,
        String memo,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime modifiedAt,
        String modifiedBy) {

    public static UserAccountDto of(String userId,Set<RoleType> roleTypes, String email, String nickname, String memo,
                          LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt, String modifiedBy) {

        return UserAccountDto.of(userId, roleTypes, email, nickname, memo, createdAt, createdBy, modifiedAt, modifiedBy);

    }
    public static UserAccountDto of(String userId, Set<RoleType> roleTypes, String email, String nickname, String memo) {

        return new UserAccountDto(userId, roleTypes, email, nickname, memo, null, null, null, null);

    }

}
