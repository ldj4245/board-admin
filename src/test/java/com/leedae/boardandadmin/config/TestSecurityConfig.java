package com.leedae.boardandadmin.config;


import com.leedae.boardandadmin.domain.constant.RoleType;
import com.leedae.boardandadmin.dto.AdminAccountDto;
import com.leedae.boardandadmin.service.AdminAccountService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.event.annotation.BeforeTestMethod;

import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@Import(SecurityConfig.class)
@TestConfiguration
public class TestSecurityConfig {

    @MockBean private AdminAccountService adminAccountService;

    @BeforeTestMethod
    public void securitySetUp(){
        given(adminAccountService.searchUser(anyString())).willReturn(Optional.of(createAdminAccountDto()));
        given(adminAccountService.saveUser(anyString(),anyString(),anySet(),anyString(),anyString(),anyString()))
                .willReturn(createAdminAccountDto());

    }

    private AdminAccountDto createAdminAccountDto(){
        return AdminAccountDto.of(
                "parkHyoShin",
                "pw",
                Set.of(RoleType.USER),
                "parkhyoShin@herbi.com",
                "park-hyo",
                "I hope many people listen to Park Hyo-shin's hero."
        );
    }

}
