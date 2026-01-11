package restapi.kculturebackend.domain.agency.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import restapi.kculturebackend.common.dto.ApiResponse;
import restapi.kculturebackend.domain.agency.dto.AgencyProfileResponse;
import restapi.kculturebackend.domain.agency.dto.CreateAgencyProfileRequest;
import restapi.kculturebackend.domain.agency.dto.UpdateAgencyProfileRequest;
import restapi.kculturebackend.domain.agency.service.AgencyService;
import restapi.kculturebackend.domain.user.entity.User;

import java.util.UUID;

/**
 * 에이전시 API 컨트롤러
 */
@Tag(name = "Agencies", description = "에이전시 관련 API")
@RestController
@RequestMapping("/api/agencies")
@RequiredArgsConstructor
public class AgencyController {

    private final AgencyService agencyService;

    /**
     * 에이전시 프로필 등록
     */
    @Operation(summary = "프로필 등록", description = "에이전시 회원가입 시 프로필을 등록합니다.")
    @PostMapping("/profile")
    public ResponseEntity<ApiResponse<AgencyProfileResponse>> createProfile(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CreateAgencyProfileRequest request) {

        AgencyProfileResponse profile = agencyService.createProfile(user, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(profile));
    }

    /**
     * 내 에이전시 프로필 조회
     */
    @Operation(summary = "내 프로필 조회", description = "로그인한 에이전시의 프로필을 조회합니다.")
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<AgencyProfileResponse>> getMyProfile(
            @AuthenticationPrincipal User user) {

        AgencyProfileResponse profile = agencyService.getMyProfile(user);
        return ResponseEntity.ok(ApiResponse.success(profile));
    }

    /**
     * 에이전시 프로필 수정
     */
    @Operation(summary = "프로필 수정", description = "에이전시 프로필 정보를 수정합니다.")
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<AgencyProfileResponse>> updateProfile(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody UpdateAgencyProfileRequest request) {

        AgencyProfileResponse profile = agencyService.updateProfile(user, request);
        return ResponseEntity.ok(ApiResponse.success(profile));
    }

    /**
     * 특정 에이전시 프로필 조회 (공개)
     */
    @Operation(summary = "에이전시 프로필 조회", description = "특정 에이전시의 공개 프로필을 조회합니다.")
    @GetMapping("/{agencyId}")
    public ResponseEntity<ApiResponse<AgencyProfileResponse>> getAgencyProfile(
            @Parameter(description = "에이전시 ID") @PathVariable UUID agencyId) {

        AgencyProfileResponse profile = agencyService.getAgencyProfile(agencyId);
        return ResponseEntity.ok(ApiResponse.success(profile));
    }
}

