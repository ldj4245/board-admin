package com.leedae.boardandadmin.dto;


import com.leedae.boardandadmin.domain.AdminAccount;
import com.leedae.boardandadmin.domain.constant.RoleType;

import java.time.LocalDateTime;
import java.util.Set;

public record AdminAccountDto(
        String userId,
        String userPassword,
        Set<RoleType> roleTypes,
        String email,
        String nickname,
        String memo,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime modifiedAt,
        String modifiedBy) {

    public static AdminAccountDto of(String userId, String userPassword, Set<RoleType> roleTypes, String email, String nickname, String memo,
                                     LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt, String modifiedBy) {

        return AdminAccountDto.of(userId, userPassword,roleTypes, email, nickname, memo, createdAt, createdBy, modifiedAt, modifiedBy);

    }
    public static AdminAccountDto of(String userId, String userPassword, Set<RoleType> roleTypes, String email, String nickname, String memo) {

        return new AdminAccountDto(userId, userPassword,roleTypes, email, nickname, memo, null, null, null, null);

    }

    public static AdminAccountDto from(AdminAccount entity){

        return new AdminAccountDto(
                entity.getUserId(),
                entity.getUserPassword(),
                entity.getRoleTypes(),
                entity.getEmail(),
                entity.getNickname(),
                entity.getMemo(),
                entity.getCreatedAt(),
                entity.getCreatedBy(),
                entity.getModifiedAt(),
                entity.getModifiedBy()
        );
    }

    public AdminAccount toEntity(){
        return AdminAccount.of(
                userId,
                userPassword,
                roleTypes,
                email,
                nickname,
                memo
        );
    }
}
