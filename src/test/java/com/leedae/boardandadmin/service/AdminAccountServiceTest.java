package com.leedae.boardandadmin.service;

import com.leedae.boardandadmin.domain.AdminAccount;
import com.leedae.boardandadmin.domain.constant.RoleType;
import com.leedae.boardandadmin.dto.AdminAccountDto;
import com.leedae.boardandadmin.repository.AdminAccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;


@DisplayName("비즈니스 로직 - 어드민 회원")
@ExtendWith(MockitoExtension.class)
class AdminAccountServiceTest {

    @InjectMocks private AdminAccountService sut;

    @Mock private AdminAccountRepository adminAccountRepository;

    @DisplayName("존재하는 회원 ID를 검색하면, 회원 데이터를 Optional로 반환한다.")
    @Test
    void givenExistentUserId_whenSearching_thenReturnsOptionalUserData(){
        //given
        String username = "lee";
        given(adminAccountRepository.findById(username)).willReturn(Optional.of(
                createAdminAccount(username)));


        //when
        Optional<AdminAccountDto> result = sut.searchUser(username);

        //then
        assertThat(result).isPresent();
        then(adminAccountRepository).should().findById(username);
    }

    @DisplayName("존재하지 않는 회원 ID를 검색하면, 비어있는 Optional을 반환한다.")
    @Test
    void givenNonexistentUserId_whenSearching_thenReturnsOptionalUserData(){
        //given
        String username = "wrong";
        given(adminAccountRepository.findById(username)).willReturn(Optional.empty());


        //when
        Optional<AdminAccountDto> result = sut.searchUser(username);

        //then
        assertThat(result).isEmpty();
        then(adminAccountRepository).should().findById(username);
    }

    @DisplayName("회원 정보를 입력하면, 새로운 회원 정보를 저장하여 가입시키고 해당 회원 데이터를 리턴한다.")
    @Test
    void givenUserParams_whenSaving_thenSavesAdminAccount(){
        //Given
        AdminAccount adminAccount = createSigningUpAdminAccount("lee", Set.of(RoleType.USER));
        given(adminAccountRepository.save(adminAccount)).willReturn(adminAccount);

        // When
        AdminAccountDto result = sut.saveUser(
                adminAccount.getUserId(),
                adminAccount.getUserPassword(),
                adminAccount.getRoleTypes(),
                adminAccount.getEmail(),
                adminAccount.getNickname(),
                adminAccount.getMemo()
        );

        // then
        assertThat(result)
                .hasFieldOrPropertyWithValue("userId",adminAccount.getUserId())
                .hasFieldOrPropertyWithValue("userPassword",adminAccount.getUserPassword())
                .hasFieldOrPropertyWithValue("roleTypes",adminAccount.getRoleTypes())
                .hasFieldOrPropertyWithValue("email",adminAccount.getEmail())
                .hasFieldOrPropertyWithValue("nickname",adminAccount.getNickname())
                .hasFieldOrPropertyWithValue("memo",adminAccount.getMemo())
                .hasFieldOrPropertyWithValue("createdBy",adminAccount.getUserId())
                .hasFieldOrPropertyWithValue("modifiedBy",adminAccount.getUserId());

        then(adminAccountRepository).should().save(adminAccount);
    }


    @DisplayName("전체 어드민 회원을 조회한다.")
    @Test
    void givenNothing_whenSelectingAdminAccounts_thenReturnsAllAdminAccounts() {
        //given
        given(adminAccountRepository.findAll()).willReturn(List.of());

        //when
        List<AdminAccountDto> result = sut.users();

        //then
        assertThat(result).hasSize(0);
        then(adminAccountRepository).should().findAll();

    }

    @DisplayName("회원 ID를 입력하면, 회원을 삭제한다.")
    @Test
    void givenUserId_whenDeleting_thenDeletesAdminAccount(){
        // Given
        String userId = "lee";
        willDoNothing().given(adminAccountRepository).deleteById(userId);

        // when
        sut.deleteUser(userId);

        // Then
        then(adminAccountRepository).should().deleteById(userId);
    }

    private AdminAccount createAdminAccount(String username) {
        return createAdminAccount(username,Set.of(RoleType.USER),null);
    }

    private AdminAccount createSigningUpAdminAccount(String username, Set<RoleType> roleTypes) {
        return createAdminAccount(username,roleTypes,username);
    }

    private AdminAccount createAdminAccount(String username, Set<RoleType> roleTypes, String createBy){
        return AdminAccount.of(
                username,
                "password",
                roleTypes,
                "l@lee.com",
                "nickname",
                "memo",
                createBy
        );
    }

}