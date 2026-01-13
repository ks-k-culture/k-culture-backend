package restapi.kculturebackend.domain.auth.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import restapi.kculturebackend.common.dto.ApiResponse;
import restapi.kculturebackend.common.exception.ErrorCode;
import restapi.kculturebackend.common.exception.UnauthorizedException;
import restapi.kculturebackend.domain.auth.dto.AuthTokens;
import restapi.kculturebackend.domain.auth.dto.ForgotPasswordRequest;
import restapi.kculturebackend.domain.auth.dto.LoginRequest;
import restapi.kculturebackend.domain.auth.dto.LoginResponse;
import restapi.kculturebackend.domain.auth.dto.RefreshTokenResponse;
import restapi.kculturebackend.domain.auth.dto.ResetPasswordRequest;
import restapi.kculturebackend.domain.auth.dto.SignupRequest;
import restapi.kculturebackend.domain.auth.dto.SignupResponse;
import restapi.kculturebackend.domain.auth.service.AuthService;
import restapi.kculturebackend.domain.user.entity.User;

@Tag(name = "Auth", description = "인증 관련 API (로그인, 회원가입, 로그아웃)")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    private static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";
    private static final long REFRESH_TOKEN_MAX_AGE = 7 * 24 * 60 * 60; // 7일 (초)

    @Value("${app.cookie.secure:false}")
    private boolean secureCookie;

    @Operation(summary = "로그인", description = "이메일/비밀번호로 로그인하여 JWT 토큰 발급")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response) {
        LoginResponse loginResponse = authService.login(request);

        // refreshToken을 httpOnly 쿠키로 설정
        ResponseCookie refreshTokenCookie = createRefreshTokenCookie(loginResponse.getRefreshToken());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        // 응답에서 refreshToken 제거 (쿠키로 전달했으므로)
        LoginResponse responseWithoutRefreshToken = LoginResponse.builder()
                .accessToken(loginResponse.getAccessToken())
                .user(loginResponse.getUser())
                .build();

        return ResponseEntity.ok(ApiResponse.success(responseWithoutRefreshToken));
    }

    @Operation(summary = "회원가입", description = "이메일/비밀번호로 회원가입 (약관 동의 필수)")
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignupResponse>> signup(@Valid @RequestBody SignupRequest request) {
        SignupResponse response = authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @Operation(summary = "로그아웃", description = "현재 세션 로그아웃 (토큰 무효화)")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @AuthenticationPrincipal UserDetails userDetails,
            HttpServletResponse response) {
        User user = (User) userDetails;
        authService.logout(user.getId().toString());

        // refreshToken 쿠키 삭제
        ResponseCookie deleteCookie = deleteRefreshTokenCookie();
        response.addHeader(HttpHeaders.SET_COOKIE, deleteCookie.toString());

        return ResponseEntity.ok(ApiResponse.success());
    }

    @Operation(summary = "토큰 갱신", description = "Refresh Token(쿠키)으로 새 Access Token 발급")
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<RefreshTokenResponse>> refreshToken(
            @CookieValue(name = REFRESH_TOKEN_COOKIE_NAME, required = false) String refreshToken,
            HttpServletResponse response) {

        if (refreshToken == null || refreshToken.isBlank()) {
            throw new UnauthorizedException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        AuthTokens tokens = authService.refreshToken(refreshToken);

        // 새 refreshToken을 쿠키에 설정
        ResponseCookie newRefreshTokenCookie = createRefreshTokenCookie(tokens.getRefreshToken());
        response.addHeader(HttpHeaders.SET_COOKIE, newRefreshTokenCookie.toString());

        // 응답에는 accessToken만 포함
        RefreshTokenResponse refreshResponse = RefreshTokenResponse.builder()
                .accessToken(tokens.getAccessToken())
                .build();

        return ResponseEntity.ok(ApiResponse.success(refreshResponse));
    }

    @Operation(summary = "비밀번호 찾기", description = "비밀번호 재설정 이메일 발송")
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Map<String, String>>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ResponseEntity.ok(ApiResponse.success(
                Map.of("message", "비밀번호 재설정 이메일이 발송되었습니다.")
        ));
    }

    @Operation(summary = "비밀번호 재설정", description = "이메일로 받은 토큰으로 새 비밀번호 설정")
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Map<String, String>>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(ApiResponse.success(
                Map.of("message", "비밀번호가 성공적으로 변경되었습니다.")
        ));
    }

    @Operation(summary = "계정 삭제", description = "계정 영구 삭제 (복구 불가)")
    @DeleteMapping("/account")
    public ResponseEntity<ApiResponse<Map<String, String>>> deleteAccount(
            @AuthenticationPrincipal UserDetails userDetails,
            HttpServletResponse response) {
        User user = (User) userDetails;
        authService.deleteAccount(user.getId().toString());

        // refreshToken 쿠키 삭제
        ResponseCookie deleteCookie = deleteRefreshTokenCookie();
        response.addHeader(HttpHeaders.SET_COOKIE, deleteCookie.toString());

        return ResponseEntity.ok(ApiResponse.success(
                Map.of("message", "계정이 삭제되었습니다.")
        ));
    }

    private ResponseCookie createRefreshTokenCookie(String refreshToken) {
        return ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, refreshToken)
                .httpOnly(true)
                .secure(secureCookie)
                .path("/")
                .sameSite("Strict")
                .maxAge(REFRESH_TOKEN_MAX_AGE)
                .build();
    }

    private ResponseCookie deleteRefreshTokenCookie() {
        return ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, "")
                .httpOnly(true)
                .secure(secureCookie)
                .path("/")
                .sameSite("Strict")
                .maxAge(0)
                .build();
    }
}
