package restapi.kculturebackend.domain.actor.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import restapi.kculturebackend.common.dto.ApiResponse;
import restapi.kculturebackend.common.dto.PaginationResponse;
import restapi.kculturebackend.domain.actor.dto.CreateShowreelRequest;
import restapi.kculturebackend.domain.actor.dto.ShowreelResponse;
import restapi.kculturebackend.domain.actor.dto.UpdateShowreelRequest;
import restapi.kculturebackend.domain.actor.service.ShowreelService;
import restapi.kculturebackend.domain.user.entity.User;

import java.util.List;
import java.util.UUID;

/**
 * 쇼릴 API 컨트롤러
 */
@Tag(name = "Showreels", description = "쇼릴(영상 포트폴리오) 관련 API")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ShowreelController {

    private final ShowreelService showreelService;

    /**
     * 특정 배우의 쇼릴 목록 조회
     */
    @Operation(summary = "배우 쇼릴 목록", description = "특정 배우의 쇼릴 목록을 조회합니다.")
    @GetMapping("/actors/{actorId}/showreels")
    public ResponseEntity<ApiResponse<List<ShowreelResponse>>> getActorShowreels(
            @Parameter(description = "배우 ID") @PathVariable UUID actorId) {

        List<ShowreelResponse> showreels = showreelService.getShowreelsByActor(actorId);
        return ResponseEntity.ok(ApiResponse.success(showreels));
    }

    /**
     * 내 쇼릴 목록 조회
     */
    @Operation(summary = "내 쇼릴 목록", description = "로그인한 배우의 쇼릴 목록을 조회합니다.")
    @GetMapping("/showreels/me")
    public ResponseEntity<ApiResponse<List<ShowreelResponse>>> getMyShowreels(
            @AuthenticationPrincipal User user) {

        List<ShowreelResponse> showreels = showreelService.getMyShowreels(user);
        return ResponseEntity.ok(ApiResponse.success(showreels));
    }

    /**
     * 인기 쇼릴 조회
     */
    @Operation(summary = "인기 쇼릴", description = "조회수 기준 인기 쇼릴을 조회합니다.")
    @GetMapping("/showreels/popular")
    public ResponseEntity<ApiResponse<PaginationResponse<ShowreelResponse>>> getPopularShowreels(
            @PageableDefault(size = 10, sort = "viewCount", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<ShowreelResponse> showreels = showreelService.getPopularShowreels(pageable);
        return ResponseEntity.ok(ApiResponse.success(PaginationResponse.from(showreels)));
    }

    /**
     * 쇼릴 상세 조회
     */
    @Operation(summary = "쇼릴 상세 조회", description = "특정 쇼릴의 상세 정보를 조회합니다. 조회수가 증가합니다.")
    @GetMapping("/showreels/{showreelId}")
    public ResponseEntity<ApiResponse<ShowreelResponse>> getShowreel(
            @Parameter(description = "쇼릴 ID") @PathVariable UUID showreelId) {

        ShowreelResponse showreel = showreelService.getShowreel(showreelId);
        return ResponseEntity.ok(ApiResponse.success(showreel));
    }

    /**
     * 쇼릴 생성
     */
    @Operation(summary = "쇼릴 생성", description = "새로운 쇼릴을 등록합니다.")
    @PostMapping("/showreels")
    public ResponseEntity<ApiResponse<ShowreelResponse>> createShowreel(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CreateShowreelRequest request) {

        ShowreelResponse showreel = showreelService.createShowreel(user, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(showreel));
    }

    /**
     * 쇼릴 수정
     */
    @Operation(summary = "쇼릴 수정", description = "쇼릴 정보를 수정합니다.")
    @PutMapping("/showreels/{showreelId}")
    public ResponseEntity<ApiResponse<ShowreelResponse>> updateShowreel(
            @AuthenticationPrincipal User user,
            @Parameter(description = "쇼릴 ID") @PathVariable UUID showreelId,
            @Valid @RequestBody UpdateShowreelRequest request) {

        ShowreelResponse showreel = showreelService.updateShowreel(user, showreelId, request);
        return ResponseEntity.ok(ApiResponse.success(showreel));
    }

    /**
     * 쇼릴 삭제
     */
    @Operation(summary = "쇼릴 삭제", description = "쇼릴을 삭제합니다.")
    @DeleteMapping("/showreels/{showreelId}")
    public ResponseEntity<ApiResponse<Void>> deleteShowreel(
            @AuthenticationPrincipal User user,
            @Parameter(description = "쇼릴 ID") @PathVariable UUID showreelId) {

        showreelService.deleteShowreel(user, showreelId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}

