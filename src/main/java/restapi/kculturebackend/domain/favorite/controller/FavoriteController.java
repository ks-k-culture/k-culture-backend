package restapi.kculturebackend.domain.favorite.controller;

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
import restapi.kculturebackend.domain.favorite.dto.CreateFavoriteRequest;
import restapi.kculturebackend.domain.favorite.dto.FavoriteResponse;
import restapi.kculturebackend.domain.favorite.entity.FavoriteType;
import restapi.kculturebackend.domain.favorite.service.FavoriteService;
import restapi.kculturebackend.domain.user.entity.User;

import java.util.Map;
import java.util.UUID;

/**
 * 찜 목록 API 컨트롤러
 */
@Tag(name = "Favorites", description = "찜 목록 관련 API (CRUD)")
@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    // 찜 목록 조회
    @Operation(summary = "찜 목록 조회", description = "로그인한 사용자의 찜 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<PaginationResponse<FavoriteResponse>>> getFavorites(
            @AuthenticationPrincipal User user,
            @Parameter(description = "찜 타입 (actor, project)") @RequestParam(required = false) FavoriteType type,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<FavoriteResponse> favorites = favoriteService.getFavorites(user, type, pageable);
        return ResponseEntity.ok(ApiResponse.success(PaginationResponse.from(favorites)));
    }

    // 찜 추가
    @Operation(summary = "찜 추가", description = "배우 또는 프로젝트를 찜 목록에 추가합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> addFavorite(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CreateFavoriteRequest request) {

        FavoriteResponse favorite = favoriteService.addFavorite(user, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(Map.of(
                "id", favorite.getId(),
                "type", favorite.getType(),
                "targetId", favorite.getTargetId()
        )));
    }

    // 찜 삭제
    @Operation(summary = "찜 삭제", description = "찜 목록에서 항목을 삭제합니다.")
    @DeleteMapping("/{favoriteId}")
    public ResponseEntity<ApiResponse<Map<String, String>>> deleteFavorite(
            @AuthenticationPrincipal User user,
            @Parameter(description = "찜 ID") @PathVariable UUID favoriteId) {

        favoriteService.deleteFavorite(user, favoriteId);
        return ResponseEntity.ok(ApiResponse.success(Map.of("message", "찜 목록에서 삭제되었습니다.")));
    }
}
