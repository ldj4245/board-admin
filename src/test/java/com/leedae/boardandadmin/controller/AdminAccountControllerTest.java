package com.leedae.boardandadmin.controller;

import com.leedae.boardandadmin.config.GlobalControllerConfig;
import com.leedae.boardandadmin.config.SecurityConfig;
import com.leedae.boardandadmin.config.TestSecurityConfig;
import com.leedae.boardandadmin.domain.constant.RoleType;
import com.leedae.boardandadmin.dto.AdminAccountDto;
import com.leedae.boardandadmin.service.AdminAccountService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.event.annotation.BeforeTestMethod;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@DisplayName("View 컨트롤러 - 어드민 회원")
@Import({SecurityConfig.class, GlobalControllerConfig.class})
@WebMvcTest(AdminAccountController.class)
class AdminAccountControllerTest {

    private final MockMvc mvc;

    @MockBean private AdminAccountService adminAccountService;

    public AdminAccountControllerTest(@Autowired MockMvc mvc) {
        this.mvc = mvc;
    }

    @BeforeTestMethod
    public void securitySetup(){
        given(adminAccountService.searchUser(anyString())).willReturn(Optional.of(createAdminAccountDto()));
        given(adminAccountService.saveUser(anyString(), anyString(), anySet(), anyString(), anyString(), anyString()))
                .willReturn(createAdminAccountDto());

    }



    @WithMockUser(username = "tester", roles = "USER")
    @DisplayName("[view][GET] 어드민 회원 페이지 - 정상 호출")
    @Test
    void givenAuthorizedUser_whenRequestingAdminMembersView_thenReturnsAdminMembersView() throws Exception{
        //Given

        //when & Then
        mvc.perform(get("/admin/members"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("admin/members"));
    }

    @WithMockUser(username = "tester", roles = "USER")
    @DisplayName("[data][GET] 어드민 회원 리스트 - 정상 호출")
    @Test
    void givenAuthorizedUser_whenRequestingAdminMembers_thenReturnsAdminMembersView() throws Exception{
        //given
        given(adminAccountService.users()).willReturn(List.of());

        //when & then
        mvc.perform(get("/api/admin/members"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
        then(adminAccountService).should().users();
    }

    @WithMockUser(username = "tester", roles = "MANAGER")
    @DisplayName("[data][DELETE] 어드민 회원 삭제 - 정상 호출")
    @Test
    void givenAuthorizedUser_whenDeletingAdminMember_thenDeletesAdminMember() throws Exception {
        //Given
        String username = "lee";
        willDoNothing().given(adminAccountService).deleteUser(username);

        // When & then
        mvc.perform(
                delete("/api/admin/members/"+ username)
                        .with(csrf())
        )
                .andExpect(status().isNoContent());

        then(adminAccountService).should().deleteUser(username);
    }

    private AdminAccountDto createAdminAccountDto() {
        return AdminAccountDto.of(
                "lee",
                "pw",
                Set.of(RoleType.USER),
                "hero@hero.com",
                "hero-test",
                "hero 짱 좋던데 ㅎㅎ.."
        );
    }


}