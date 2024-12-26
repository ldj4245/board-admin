package com.leedae.boardandadmin.domain;

import com.leedae.boardandadmin.domain.constant.RoleType;
import com.leedae.boardandadmin.domain.converter.RoleTypesConverter;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@ToString(callSuper = true)
@Table(indexes = {
        @Index(columnList = "email", unique = true),
        @Index(columnList = "createdAt"),
        @Index(columnList = "createdBy")
})
@Entity
public class AdminAccount extends AuditingFields {

    @Id
    @Column(length = 50)
    private String userId;

    @Setter @Column(nullable = false) private String userPassword;

    @Convert(converter = RoleTypesConverter.class)
    @Column(nullable = false)
    private Set<RoleType> roleTypes = new LinkedHashSet<>();

    @Setter @Column(length = 100) private String email;
    @Setter @Column(length = 100) private String nickname;
    @Setter private String memo;


    protected AdminAccount() {}

    private AdminAccount(String userId, String userPassword, Set<RoleType> roleTypes, String email, String nickname, String memo, String createdBy) {
        this.userId = userId;
        this.userPassword = userPassword;
        this.roleTypes = roleTypes;
        this.email = email;
        this.nickname = nickname;
        this.memo = memo;
        this.createdBy = createdBy;
        this.modifiedBy = createdBy;
    }


    public static AdminAccount of(String userId, String userPassword, Set<RoleType> roletypes, String email, String nickname, String memo) {
        return new AdminAccount(userId, userPassword,roletypes, email, nickname, memo,null);
    }


    public static AdminAccount of(String userId, String userPassword, Set<RoleType> roleTypes, String email, String nickname, String memo, String createdBy) {
        return new AdminAccount(userId, userPassword,roleTypes, email, nickname, memo,createdBy);
    }

    public void addRoleType(RoleType roleType) {
        this.getRoleTypes().add(roleType);
    }

    public void addRoleType(Collection<RoleType> roleTypes) {
        this.getRoleTypes().addAll(roleTypes);
    }

    public  void removeRoleType(RoleType roleType) {
        this.getRoleTypes().remove(roleType);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof AdminAccount that)) return false;
        return Objects.equals(getUserId(), that.getUserId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getUserId());
    }
}