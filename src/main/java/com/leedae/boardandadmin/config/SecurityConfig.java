package com.leedae.boardandadmin.config;


import com.leedae.boardandadmin.domain.constant.RoleType;
import com.leedae.boardandadmin.dto.security.BoardAdminPrincipal;
import com.leedae.boardandadmin.dto.security.KakaoOAuth2Response;
import com.leedae.boardandadmin.service.AdminAccountService;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;


import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {
        String[] rolesAboveManager = {RoleType.MANAGER.name(), RoleType.ADMIN.name(), RoleType.DEVELOPER.name()};

        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .mvcMatchers(HttpMethod.POST,"/**").hasAnyRole(rolesAboveManager)
                        .mvcMatchers(HttpMethod.DELETE,"/**").hasAnyRole(rolesAboveManager)
                        .anyRequest().permitAll()
                )
                .formLogin(withDefaults())
                .logout(logout -> logout.logoutSuccessUrl("/"))
                .oauth2Login(withDefaults())
                .build();
    }

    @Bean
    public UserDetailsService userDetailsService(AdminAccountService adminAccountService) {
        return username -> adminAccountService
                .searchUser(username)
                .map(BoardAdminPrincipal::from)
                .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다 - username: " + username));
    }

    private OAuth2User processKakaoOAuth2User(
            OAuth2UserRequest userRequest,
            OAuth2User oAuth2User,
            AdminAccountService adminAccountService,
            PasswordEncoder passwordEncoder
    ) {
        KakaoOAuth2Response kakaoResponse = KakaoOAuth2Response.from(oAuth2User.getAttributes());
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String providerId = String.valueOf(kakaoResponse.id());
        Set<RoleType> roleTypes = Set.of(RoleType.USER);

        return getOAuth2User(
                adminAccountService,
                passwordEncoder,
                registrationId,
                providerId,
                roleTypes,
                kakaoResponse.email(),
                kakaoResponse.nickname()
        );
    }

    private OAuth2User processNaverOAuth2User(
            OAuth2UserRequest userRequest,
            OAuth2User oAuth2User,
            AdminAccountService adminAccountService,
            PasswordEncoder passwordEncoder
    ) {
        Map<String, Object> response = (Map<String, Object>) oAuth2User.getAttributes().get("response");
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String providerId = (String) response.get("id");
        Set<RoleType> roleTypes = Set.of(RoleType.USER);

        return getOAuth2User(
                adminAccountService,
                passwordEncoder,
                registrationId,
                providerId,
                roleTypes,
                (String) response.get("email"),
                (String) response.get("nickname")
        );
    }

    private OAuth2User getOAuth2User(
            AdminAccountService adminAccountService,
            PasswordEncoder passwordEncoder,
            String registrationId,
            String providerId,
            Set<RoleType> roleTypes,
            String email,
            String nickname
    ) {
        String username = registrationId + "_" + providerId;
        String password = passwordEncoder.encode("{bcrypt}" + UUID.randomUUID());

        return adminAccountService.searchUser(username)
                .map(BoardAdminPrincipal::from)
                .orElseGet(() ->
                        BoardAdminPrincipal.from(
                                adminAccountService.saveUser(
                                        username,
                                        password,
                                        roleTypes,
                                        email,
                                        nickname,
                                        null
                                )
                        )
                );
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

}