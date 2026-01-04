package restapi.kculturebackend.domain.actor.controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import restapi.kculturebackend.common.dto.ApiResponse;
import restapi.kculturebackend.common.dto.PaginationResponse;
import restapi.kculturebackend.domain.actor.dto.ActorDetailResponse;
import restapi.kculturebackend.domain.actor.dto.ActorSummaryResponse;
import restapi.kculturebackend.domain.actor.dto.UpdateActorProfileRequest;
import restapi.kculturebackend.domain.actor.service.ActorService;
import restapi.kculturebackend.domain.user.entity.User;

/**
 * 배우 API 컨트롤러
 */
@Tag(name = "Actors", description = "배우 관련 API")
@RestController
@RequestMapping("/api/actors")
@RequiredArgsConstructor
public class ActorController {

    private final ActorService actorService;

    /**
     * 배우 목록 조회
     */
    @Operation(summary = "배우 목록 조회", description = "프로필이 완성된 배우 목록을 페이징하여 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<PaginationResponse<ActorSummaryResponse>>> getActors(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        
        Page<ActorSummaryResponse> actors = actorService.getActors(pageable);
        return ResponseEntity.ok(ApiResponse.success(PaginationResponse.from(actors)));
    }

    /**
     * 배우 검색
     */
    @Operation(summary = "배우 검색", description = "이름/활동명으로 배우를 검색합니다.")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PaginationResponse<ActorSummaryResponse>>> searchActors(
            @Parameter(description = "검색할 이름") @RequestParam(required = false) String name,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        
        Page<ActorSummaryResponse> actors = actorService.searchActors(name, pageable);
        return ResponseEntity.ok(ApiResponse.success(PaginationResponse.from(actors)));
    }

    /**
     * 배우 상세 조회
     */
    @Operation(summary = "배우 상세 조회", description = "특정 배우의 상세 정보를 조회합니다.")
    @GetMapping("/{actorId}")
    public ResponseEntity<ApiResponse<ActorDetailResponse>> getActorDetail(
            @Parameter(description = "배우 ID") @PathVariable UUID actorId) {
        
        ActorDetailResponse actor = actorService.getActorDetail(actorId);
        return ResponseEntity.ok(ApiResponse.success(actor));
    }

    /**
     * 내 배우 프로필 조회
     */
    @Operation(summary = "내 프로필 조회", description = "로그인한 배우의 프로필을 조회합니다.")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<ActorDetailResponse>> getMyProfile(
            @AuthenticationPrincipal User user) {
        
        ActorDetailResponse profile = actorService.getMyProfile(user);
        return ResponseEntity.ok(ApiResponse.success(profile));
    }

    /**
     * 배우 프로필 수정
     */
    @Operation(summary = "프로필 수정", description = "배우 프로필 정보를 수정합니다.")
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<ActorDetailResponse>> updateProfile(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody UpdateActorProfileRequest request) {
        
        ActorDetailResponse profile = actorService.updateProfile(user, request);
        return ResponseEntity.ok(ApiResponse.success(profile));
    }
}

