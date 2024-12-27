package com.leedae.boardandadmin.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leedae.boardandadmin.domain.constant.RoleType;
import com.leedae.boardandadmin.dto.ArticleDto;
import com.leedae.boardandadmin.dto.UserAccountDto;
import com.leedae.boardandadmin.dto.properties.ProjectProperties;
import com.leedae.boardandadmin.dto.response.ArticleClientResponse;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;


@ActiveProfiles("test")
@DisplayName("비즈니스 로직 - 게시글 관리")
class ArticleManagementServiceTest {

    //restTemplate을 가져다가 이것을 모킹할 것인가 직접 가져다 쓸 것인가
    //모킹해서 쓸 거면 외부 서비스가 장애가 생겨도 모킹 했기 때문에 우리 서비스에선 별 다른 영향이 없음.

    //    @Disabled("실제 API 호출 결과 관찰용이므로 평상시엔 비활성화")
    @DisplayName("실제 API 호출 테스트")
    @SpringBootTest
    @Nested
    class RealApiTest {

        private final ArticleManagementService sut;

        @Autowired

        public RealApiTest(ArticleManagementService sut) {
            this.sut = sut;
        }

        @DisplayName("게시글 API를 호출하면, 게시글을 가져온다.")
        @Test
        void given_when_then(){
            // Given

            // When
            List<ArticleDto> result = sut.getArticles();

            // Then
            System.out.println(result.stream().findFirst());
            assertThat(result).isNotNull();

        }
    }

    @DisplayName("API mocking 테스트")
    @EnableConfigurationProperties(ProjectProperties.class)
    @AutoConfigureWebClient(registerRestTemplate = true)
    @RestClientTest(ArticleManagementService.class)
    @Nested //중첩 테스트
    class RestTemplateTest {


        private final ArticleManagementService sut;

        private final ProjectProperties projectProperties; //url 직접 가져오기
        private final MockRestServiceServer server;
        private final ObjectMapper mapper;

        @Autowired
        public RestTemplateTest(
                ArticleManagementService sut, ProjectProperties projectProperties,
                MockRestServiceServer server, ObjectMapper mapper
        ) {
            this.sut = sut;
            this.projectProperties = projectProperties;
            this.server = server;
            this.mapper = mapper;
        }


        @DisplayName("게시글 목록 API를 호출하면, 게시글들을 가져온다.")
        @Test
        void givenNothing_whenCallingArticlesApi_thenReturnsArticleList() throws JsonProcessingException {
            //given
            ArticleDto expectedArticle = createArticleDto("제목", "글");
            ArticleClientResponse expectedResponse = ArticleClientResponse.of(List.of(expectedArticle));

            //when
            List<ArticleDto> result = sut.getArticles();
            server
                    .expect(requestTo(projectProperties.board().url() + "/api/articles?size=10000"))
                    .andRespond(MockRestResponseCreators.withSuccess(
                            mapper.writeValueAsBytes(expectedResponse),
                            MediaType.APPLICATION_JSON
                    ));

            //then
            assertThat(result).first()
                    .hasFieldOrPropertyWithValue("id", expectedArticle.id())
                    .hasFieldOrPropertyWithValue("title", expectedArticle.title())
                    .hasFieldOrPropertyWithValue("content", expectedArticle.content())
                    .hasFieldOrPropertyWithValue("userAccount.nickname", expectedArticle.userAccount().nickname());
            server.verify();
        }

        @DisplayName("게시글 ID와 함께 게시글 API을 호출하면, 게시글을 가져온다.")        @Test
        void givenNothing_whenCallingArticleApi_thenReturnsArticle() throws Exception {
            //given
            Long articleId = 1L;
            ArticleDto expectedArticle = createArticleDto("게시판", "글");
            server
                    .expect(requestTo(projectProperties.board().url() + "/api/articles/" + articleId))
                    .andRespond(MockRestResponseCreators.withSuccess(
                            mapper.writeValueAsBytes(expectedArticle),
                            MediaType.APPLICATION_JSON
                    ));

            //when
            ArticleDto result = sut.getArticle(articleId);

            //then
            assertThat(result)
                    .hasFieldOrPropertyWithValue("id", expectedArticle.id())
                    .hasFieldOrPropertyWithValue("title", expectedArticle.title())
                    .hasFieldOrPropertyWithValue("content", expectedArticle.content())
                    .hasFieldOrPropertyWithValue("userAccount.nickname", expectedArticle.userAccount().nickname());
            server.verify();
        }

        @DisplayName("게시글 ID와 함께 게시글 삭제 API를 호출하면, 게시글을 삭제한다.")
        @Test
        void givenArticleId_whenCallingDeleteArticleApi_thenDeletesAnArticle() throws JsonProcessingException {
            //given
            Long articeId = 1L;
            server
                    .expect(requestTo(projectProperties.board().url() + "/api/articles/" + articeId))
                    .andExpect(method(HttpMethod.DELETE))
                    .andRespond(withSuccess());
            //when
            sut.deleteArticle(articeId);

            //then
            server.verify();
        }


    }

    private ArticleDto createArticleDto(String title, String content) {
        return ArticleDto.of(
                1L,
                createUserAccountDto(),
                title,
                content,
                null,
                LocalDateTime.now(),
                "Lee",
                LocalDateTime.now(),
                "Lee"

        );
    }

    private UserAccountDto createUserAccountDto() {
        return UserAccountDto.of(
                "leeTest",
                "lee-test@gmail.com",
                "lee-test",
                "test memo"
        );
    }


}