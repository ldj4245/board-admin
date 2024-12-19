package com.leedae.boardandadmin.dto.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DTO - Naver OAuth 2.0 인증 응답 데이터 테스트")
class NaverOAuth2ResponseTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @DisplayName("인증 결과를 Map으로 받으면, 네이버 인증 응답 객체로 변환한다.")
    @Test
    void givenMapFromJson_whenInstantiating_thenReturnsNaverResponseObject() throws Exception {
        // Given
        String serializedResponse = """
                {
                    "resultcode": "00",
                    "message": "success",
                    "response": {
                        "id": "12345678",
                        "email": "test@naver.com",
                        "name": "홍길동",
                        "nickname": "길동이"
                    }
                }
                """;
        Map<String, Object> attributes = mapper.readValue(serializedResponse, new TypeReference<>(){});

        // When
        NaverOAuth2Response result = NaverOAuth2Response.from(attributes);

        // Then
        assertThat(result)
                .hasFieldOrPropertyWithValue("resultcode", "00")
                .hasFieldOrPropertyWithValue("message", "success");

        assertThat(result.response())
                .hasFieldOrPropertyWithValue("id", "12345678")
                .hasFieldOrPropertyWithValue("email", "test@naver.com")
                .hasFieldOrPropertyWithValue("name", "홍길동")
                .hasFieldOrPropertyWithValue("nickname", "길동이");
    }
}