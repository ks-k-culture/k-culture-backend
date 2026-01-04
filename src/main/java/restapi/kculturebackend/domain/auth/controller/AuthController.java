package restapi.kculturebackend.domain.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import restapi.kculturebackend.common.dto.ApiResponse;
import restapi.kculturebackend.domain.auth.dto.*;
import restapi.kculturebackend.domain.auth.service.AuthService;
import restapi.kculturebackend.domain.user.entity.User;

import java.util.Map;

/**
 * 인증 관련 API 컨트롤러
 */
@Tag(name = "Auth", description = "인증 관련 API (로그인, 회원가입, 로그아웃)")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 로그인
     */
    @Operation(summary = "로그인", description = "이메일/비밀번호로 로그인하여 JWT 토큰 발급")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 회원가입
     */
    @Operation(summary = "회원가입", description = "이메일/비밀번호로 회원가입 (약관 동의 필수)")
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignupResponse>> signup(@Valid @RequestBody SignupRequest request) {
        SignupResponse response = authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    /**
     * 로그아웃
     */
    @Operation(summary = "로그아웃", description = "현재 세션 로그아웃 (토큰 무효화)")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@AuthenticationPrincipal UserDetails userDetails) {
        User user = (User) userDetails;
        authService.logout(user.getId().toString());
        return ResponseEntity.ok(ApiResponse.success());
    }

    /**
     * 토큰 갱신
     */
    @Operation(summary = "토큰 갱신", description = "Refresh Token으로 새 Access Token 발급")
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthTokens>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        AuthTokens tokens = authService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.success(tokens));
    }

    /**
     * 비밀번호 찾기
     */
    @Operation(summary = "비밀번호 찾기", description = "비밀번호 재설정 이메일 발송")
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Map<String, String>>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ResponseEntity.ok(ApiResponse.success(
                Map.of("message", "비밀번호 재설정 이메일이 발송되었습니다.")
        ));
    }

    /**
     * 비밀번호 재설정
     */
    @Operation(summary = "비밀번호 재설정", description = "이메일로 받은 토큰으로 새 비밀번호 설정")
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Map<String, String>>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(ApiResponse.success(
                Map.of("message", "비밀번호가 성공적으로 변경되었습니다.")
        ));
    }

    /**
     * 계정 삭제
     */
    @Operation(summary = "계정 삭제", description = "계정 영구 삭제 (복구 불가)")
    @DeleteMapping("/account")
    public ResponseEntity<ApiResponse<Map<String, String>>> deleteAccount(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = (User) userDetails;
        authService.deleteAccount(user.getId().toString());
        return ResponseEntity.ok(ApiResponse.success(
                Map.of("message", "계정이 삭제되었습니다.")
        ));
    }
}

