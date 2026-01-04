package restapi.kculturebackend.integration;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatusCode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.testcontainers.junit.jupiter.Testcontainers;
import restapi.kculturebackend.config.TestContainersConfig;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 인증 API 통합 테스트 (Testcontainers 사용)
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("test")
@Import(TestContainersConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthIntegrationTest {

    @LocalServerPort
    private int port;

    private RestClient restClient;

    private static String accessToken;
    private static final String TEST_EMAIL = "integration-test@example.com";
    private static final String TEST_PASSWORD = "password123";

    @BeforeEach
    void setUp() {
        restClient = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    @Test
    @Order(1)
    @DisplayName("회원가입 성공")
    void signup_Success() {
        // given
        Map<String, Object> requestBody = Map.of(
                "email", TEST_EMAIL,
                "password", TEST_PASSWORD,
                "passwordConfirm", TEST_PASSWORD,
                "type", "ACTOR",
                "termsAgreed", true,
                "privacyAgreed", true,
                "marketingAgreed", false
        );

        // when
        Map response = restClient.post()
                .uri("/api/auth/signup")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .body(Map.class);

        // then
        assertThat(response).isNotNull();
        assertThat(response.get("success")).isEqualTo(true);
        
        Map<String, Object> data = (Map<String, Object>) response.get("data");
        assertThat(data.get("email")).isEqualTo(TEST_EMAIL);
        assertThat(data.get("type")).isEqualTo("ACTOR");
    }

    @Test
    @Order(2)
    @DisplayName("로그인 성공")
    void login_Success() {
        // given
        Map<String, Object> requestBody = Map.of(
                "email", TEST_EMAIL,
                "password", TEST_PASSWORD
        );

        // when
        Map response = restClient.post()
                .uri("/api/auth/login")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .body(Map.class);

        // then
        assertThat(response).isNotNull();
        assertThat(response.get("success")).isEqualTo(true);
        
        Map<String, Object> data = (Map<String, Object>) response.get("data");
        assertThat(data.get("accessToken")).isNotNull();
        assertThat(data.get("refreshToken")).isNotNull();
        
        // accessToken 저장 (로그아웃 테스트용)
        accessToken = (String) data.get("accessToken");
    }

    @Test
    @Order(3)
    @DisplayName("로그아웃 성공")
    void logout_Success() {
        // when
        Map response = restClient.post()
                .uri("/api/auth/logout")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .body(Map.class);

        // then
        assertThat(response).isNotNull();
        assertThat(response.get("success")).isEqualTo(true);
    }

    @Test
    @Order(4)
    @DisplayName("회원가입 실패 - 이메일 중복")
    void signup_Fail_EmailExists() {
        // given
        Map<String, Object> requestBody = Map.of(
                "email", TEST_EMAIL,
                "password", TEST_PASSWORD,
                "passwordConfirm", TEST_PASSWORD,
                "type", "ACTOR",
                "termsAgreed", true,
                "privacyAgreed", true,
                "marketingAgreed", false
        );

        // when & then
        assertThatThrownBy(() -> 
                restClient.post()
                        .uri("/api/auth/signup")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .body(requestBody)
                        .retrieve()
                        .body(Map.class))
                .isInstanceOf(HttpClientErrorException.Conflict.class);
    }

    @Test
    @Order(5)
    @DisplayName("로그인 실패 - 잘못된 비밀번호")
    void login_Fail_WrongPassword() {
        // given
        Map<String, Object> requestBody = Map.of(
                "email", TEST_EMAIL,
                "password", "wrongpassword"
        );

        // when & then
        assertThatThrownBy(() -> 
                restClient.post()
                        .uri("/api/auth/login")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .body(requestBody)
                        .retrieve()
                        .body(Map.class))
                .isInstanceOf(HttpClientErrorException.Unauthorized.class);
    }

    @Test
    @Order(6)
    @DisplayName("회원가입 실패 - 비밀번호 불일치")
    void signup_Fail_PasswordMismatch() {
        // given
        Map<String, Object> requestBody = Map.of(
                "email", "new-user@example.com",
                "password", "password123",
                "passwordConfirm", "differentPassword",
                "type", "ACTOR",
                "termsAgreed", true,
                "privacyAgreed", true,
                "marketingAgreed", false
        );

        // when & then
        assertThatThrownBy(() -> 
                restClient.post()
                        .uri("/api/auth/signup")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .body(requestBody)
                        .retrieve()
                        .body(Map.class))
                .isInstanceOf(HttpClientErrorException.BadRequest.class);
    }
}
