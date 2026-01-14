package restapi.kculturebackend.domain.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import restapi.kculturebackend.common.dto.ApiResponse;
import restapi.kculturebackend.domain.user.dto.ChangePasswordRequest;
import restapi.kculturebackend.domain.user.dto.NotificationSettingsDto;
import restapi.kculturebackend.domain.user.dto.UpdateProfileRequest;
import restapi.kculturebackend.domain.user.dto.UserProfileResponse;
import restapi.kculturebackend.domain.user.entity.User;
import restapi.kculturebackend.domain.user.service.UserService;

/**
 * 사용자 프로필 및 설정 컨트롤러
 */
@Tag(name = "Users", description = "사용자 설정 관련 API (프로필, 알림)")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 내 정보 조회
     */
    @Operation(summary = "내 정보 조회")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getMyProfile(
            @AuthenticationPrincipal User user) {
        UserProfileResponse response = userService.getMyProfile(user.getId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 프로필 수정
     */
    @Operation(summary = "프로필 수정")
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(
            @AuthenticationPrincipal User user,
            @RequestBody UpdateProfileRequest request) {
        UserProfileResponse response = userService.updateProfile(user.getId(), request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 알림 설정 조회
     */
    @Operation(summary = "알림 설정 조회")
    @GetMapping("/settings/notifications")
    public ResponseEntity<ApiResponse<NotificationSettingsDto>> getNotificationSettings(
            @AuthenticationPrincipal User user) {
        NotificationSettingsDto response = userService.getNotificationSettings(user.getId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 알림 설정 수정
     */
    @Operation(summary = "알림 설정 수정")
    @PutMapping("/settings/notifications")
    public ResponseEntity<ApiResponse<NotificationSettingsDto>> updateNotificationSettings(
            @AuthenticationPrincipal User user,
            @RequestBody NotificationSettingsDto request) {
        NotificationSettingsDto response = userService.updateNotificationSettings(user.getId(), request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 비밀번호 변경
     */
    @Operation(summary = "비밀번호 변경")
    @PutMapping("/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(user.getId(), request);
        return ResponseEntity.ok(ApiResponse.success());
    }
}

