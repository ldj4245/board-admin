package com.leedae.boardandadmin.controller;

import com.leedae.boardandadmin.config.GlobalControllerConfig;
import com.leedae.boardandadmin.config.SecurityConfig;
import com.leedae.boardandadmin.config.TestSecurityConfig;
import com.leedae.boardandadmin.dto.UserAccountDto;
import com.leedae.boardandadmin.service.UserAccountManagementService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@DisplayName("View 컨트롤러 - 회원 관리")
@Import({TestSecurityConfig.class, GlobalControllerConfig.class})
@WebMvcTest(UserAccountManagementController.class)
class UserAccountManagementControllerTest {

    private final MockMvc mvc;

    @MockBean
    private UserAccountManagementService userAccountManagementService;


    UserAccountManagementControllerTest(@Autowired MockMvc mvc) {
        this.mvc = mvc;
    }

    @WithMockUser(username = "tester",roles = "USER")
    @DisplayName("[view][GET] 회원 관리 페이지 - 정상 호출")
    @Test
    void givenNothing_whenRequestingUserAccountManagementView_thenReturnsUserAccountManagementView() throws Exception {
        //given
        given(userAccountManagementService.getUserAccounts()).willReturn(List.of());
        //when & Then
        mvc.perform(get("/management/user-accounts"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("management/user-accounts"))
                .andExpect(model().attribute("userAccounts", List.of()));
        then(userAccountManagementService).should().getUserAccounts();

    }

    @WithMockUser(username = "tester",roles = "USER")
    @DisplayName("[data][GET] 회원 1개 - 정상 호출")
    @Test
    void givenUserAccountId_whenRequestingUserAccount_thenReturnsUserAccount() throws Exception {
        //given
        String userId = "lee";
        UserAccountDto userAccountDto = createUserAccountDto(userId, "Lee");
        given(userAccountManagementService.getUserAccount(userId)).willReturn(userAccountDto);

        // When & Then

        mvc.perform(get("/management/user-accounts/" + userId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.nickname").value(userAccountDto.nickname()));
        then(userAccountManagementService).should().getUserAccount(userId);
    }


    @WithMockUser(username = "tester",roles = "MANAGER")
    @DisplayName("[view][POST] 회원 삭제 - 정상 호출")
    @Test
    void givenUserAccountId_WenRequestingDeletion_thenRedirectsToUserAccountManagementView() throws Exception {
        String userId = "lee";
        willDoNothing().given(userAccountManagementService).deleteUserAccount(userId);


        // when & Then
        mvc.perform(
                post("/management/user-accounts/" + userId)
                        .with(csrf())
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/management/user-accounts"));
        then(userAccountManagementService).should().deleteUserAccount(userId);

    }

    private UserAccountDto createUserAccountDto(String userId, String nickname) {
        return UserAccountDto.of(
                userId,
                "lee@gmail.com",
                nickname,
                "test memo"
        );
    }
}