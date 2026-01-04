package restapi.kculturebackend.domain.actor.controller;

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
import restapi.kculturebackend.domain.actor.dto.CreateFilmographyRequest;
import restapi.kculturebackend.domain.actor.dto.FilmographyResponse;
import restapi.kculturebackend.domain.actor.dto.UpdateFilmographyRequest;
import restapi.kculturebackend.domain.actor.service.FilmographyService;
import restapi.kculturebackend.domain.user.entity.User;

import java.util.List;
import java.util.UUID;

/**
 * 필모그래피 API 컨트롤러
 */
@Tag(name = "Filmography", description = "필모그래피 관련 API")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FilmographyController {

    private final FilmographyService filmographyService;

    /**
     * 특정 배우의 필모그래피 목록 조회
     */
    @Operation(summary = "배우 필모그래피 목록", description = "특정 배우의 필모그래피 목록을 조회합니다.")
    @GetMapping("/actors/{actorId}/filmography")
    public ResponseEntity<ApiResponse<List<FilmographyResponse>>> getActorFilmographies(
            @Parameter(description = "배우 ID") @PathVariable UUID actorId) {

        List<FilmographyResponse> filmographies = filmographyService.getFilmographiesByActor(actorId);
        return ResponseEntity.ok(ApiResponse.success(filmographies));
    }

    /**
     * 내 필모그래피 목록 조회
     */
    @Operation(summary = "내 필모그래피 목록", description = "로그인한 배우의 필모그래피 목록을 조회합니다.")
    @GetMapping("/filmography/me")
    public ResponseEntity<ApiResponse<List<FilmographyResponse>>> getMyFilmographies(
            @AuthenticationPrincipal User user) {

        List<FilmographyResponse> filmographies = filmographyService.getMyFilmographies(user);
        return ResponseEntity.ok(ApiResponse.success(filmographies));
    }

    /**
     * 필모그래피 상세 조회
     */
    @Operation(summary = "필모그래피 상세 조회", description = "특정 필모그래피의 상세 정보를 조회합니다.")
    @GetMapping("/filmography/{filmographyId}")
    public ResponseEntity<ApiResponse<FilmographyResponse>> getFilmography(
            @Parameter(description = "필모그래피 ID") @PathVariable UUID filmographyId) {

        FilmographyResponse filmography = filmographyService.getFilmography(filmographyId);
        return ResponseEntity.ok(ApiResponse.success(filmography));
    }

    /**
     * 필모그래피 생성
     */
    @Operation(summary = "필모그래피 생성", description = "새로운 필모그래피를 등록합니다.")
    @PostMapping("/filmography")
    public ResponseEntity<ApiResponse<FilmographyResponse>> createFilmography(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CreateFilmographyRequest request) {

        FilmographyResponse filmography = filmographyService.createFilmography(user, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(filmography));
    }

    /**
     * 필모그래피 수정
     */
    @Operation(summary = "필모그래피 수정", description = "필모그래피 정보를 수정합니다.")
    @PutMapping("/filmography/{filmographyId}")
    public ResponseEntity<ApiResponse<FilmographyResponse>> updateFilmography(
            @AuthenticationPrincipal User user,
            @Parameter(description = "필모그래피 ID") @PathVariable UUID filmographyId,
            @Valid @RequestBody UpdateFilmographyRequest request) {

        FilmographyResponse filmography = filmographyService.updateFilmography(user, filmographyId, request);
        return ResponseEntity.ok(ApiResponse.success(filmography));
    }

    /**
     * 필모그래피 삭제
     */
    @Operation(summary = "필모그래피 삭제", description = "필모그래피를 삭제합니다.")
    @DeleteMapping("/filmography/{filmographyId}")
    public ResponseEntity<ApiResponse<Void>> deleteFilmography(
            @AuthenticationPrincipal User user,
            @Parameter(description = "필모그래피 ID") @PathVariable UUID filmographyId) {

        filmographyService.deleteFilmography(user, filmographyId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}

