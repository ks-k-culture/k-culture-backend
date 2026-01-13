package restapi.kculturebackend.integration;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.testcontainers.junit.jupiter.Testcontainers;

import restapi.kculturebackend.config.TestContainersConfig;

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
    private static String refreshTokenCookie;
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
    @DisplayName("로그인 성공 - accessToken은 body, refreshToken은 쿠키로 전달")
    void login_Success() {
        // given
        Map<String, Object> requestBody = Map.of(
                "email", TEST_EMAIL,
                "password", TEST_PASSWORD
        );

        // when
        ResponseEntity<Map> responseEntity = restClient.post()
                .uri("/api/auth/login")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .toEntity(Map.class);

        // then
        assertThat(responseEntity.getStatusCode().is2xxSuccessful()).isTrue();
        
        Map response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.get("success")).isEqualTo(true);
        
        Map<String, Object> data = (Map<String, Object>) response.get("data");
        assertThat(data.get("accessToken")).isNotNull();
        // refreshToken은 body에 없어야 함 (쿠키로 전달)
        assertThat(data.get("refreshToken")).isNull();
        
        // Set-Cookie 헤더에 refreshToken이 있는지 확인
        List<String> cookies = responseEntity.getHeaders().get(HttpHeaders.SET_COOKIE);
        assertThat(cookies).isNotNull();
        assertThat(cookies).anyMatch(cookie -> cookie.startsWith("refreshToken="));
        
        // accessToken 저장 (로그아웃 테스트용)
        accessToken = (String) data.get("accessToken");
        
        // refreshToken 쿠키 저장 (토큰 갱신 테스트용)
        refreshTokenCookie = cookies.stream()
                .filter(cookie -> cookie.startsWith("refreshToken="))
                .findFirst()
                .orElse(null);
    }

    @Test
    @Order(3)
    @DisplayName("토큰 갱신 성공 - 쿠키의 refreshToken으로 새 accessToken 발급")
    void refreshToken_Success() {
        // given - refreshTokenCookie에서 쿠키 값만 추출
        assertThat(refreshTokenCookie).isNotNull();
        String cookieValue = refreshTokenCookie.split(";")[0]; // "refreshToken=xxx" 부분만

        // when
        ResponseEntity<Map> responseEntity = restClient.post()
                .uri("/api/auth/refresh")
                .header(HttpHeaders.COOKIE, cookieValue)
                .retrieve()
                .toEntity(Map.class);

        // then
        assertThat(responseEntity.getStatusCode().is2xxSuccessful()).isTrue();
        
        Map response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.get("success")).isEqualTo(true);
        
        Map<String, Object> data = (Map<String, Object>) response.get("data");
        assertThat(data.get("accessToken")).isNotNull();
        
        // 새로운 refreshToken 쿠키가 설정되어야 함
        List<String> cookies = responseEntity.getHeaders().get(HttpHeaders.SET_COOKIE);
        assertThat(cookies).isNotNull();
        assertThat(cookies).anyMatch(cookie -> cookie.startsWith("refreshToken="));
    }

    @Test
    @Order(4)
    @DisplayName("로그아웃 성공")
    void logout_Success() {
        // when
        ResponseEntity<Map> responseEntity = restClient.post()
                .uri("/api/auth/logout")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .toEntity(Map.class);

        // then
        assertThat(responseEntity.getStatusCode().is2xxSuccessful()).isTrue();
        
        Map response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.get("success")).isEqualTo(true);
        
        // refreshToken 쿠키가 삭제되어야 함 (maxAge=0)
        List<String> cookies = responseEntity.getHeaders().get(HttpHeaders.SET_COOKIE);
        assertThat(cookies).isNotNull();
        assertThat(cookies).anyMatch(cookie -> 
                cookie.startsWith("refreshToken=") && cookie.contains("Max-Age=0"));
    }

    @Test
    @Order(5)
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
    @Order(6)
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
    @Order(7)
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

    @Test
    @Order(8)
    @DisplayName("토큰 갱신 실패 - 쿠키 없음")
    void refreshToken_Fail_NoCookie() {
        // when & then
        assertThatThrownBy(() -> 
                restClient.post()
                        .uri("/api/auth/refresh")
                        .retrieve()
                        .body(Map.class))
                .isInstanceOf(HttpClientErrorException.Unauthorized.class);
    }
}
