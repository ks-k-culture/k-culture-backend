package restapi.kculturebackend.domain.actor.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import restapi.kculturebackend.common.dto.ApiResponse;
import restapi.kculturebackend.common.dto.PaginationResponse;
import restapi.kculturebackend.domain.actor.dto.ActorDetailResponse;
import restapi.kculturebackend.domain.actor.dto.ActorRecommendRequest;
import restapi.kculturebackend.domain.actor.dto.ActorRecommendResponse;
import restapi.kculturebackend.domain.actor.dto.ActorSummaryResponse;
import restapi.kculturebackend.domain.actor.dto.ContactActorRequest;
import restapi.kculturebackend.domain.actor.dto.CreateActorProfileRequest;
import restapi.kculturebackend.domain.actor.dto.UpdateActorProfileRequest;
import restapi.kculturebackend.domain.actor.service.ActorService;
import restapi.kculturebackend.domain.user.entity.User;
import restapi.kculturebackend.infrastructure.storage.FileStorageService;
import restapi.kculturebackend.infrastructure.storage.FileType;
import restapi.kculturebackend.infrastructure.storage.UploadResult;

/**
 * 배우 API 컨트롤러
 */
@Tag(name = "Actors", description = "배우 관련 API")
@RestController
@RequestMapping("/api/actors")
@RequiredArgsConstructor
public class ActorController {

    private final ActorService actorService;
    private final FileStorageService fileStorageService;

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

    /**
     * 프로필 이미지 업로드
     */
    @Operation(summary = "프로필 이미지 업로드", description = "배우 프로필 이미지를 업로드합니다.")
    @PutMapping(value = "/me/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadProfileImage(
            @AuthenticationPrincipal User user,
            @RequestPart("image") MultipartFile image) {
        
        if (image == null || image.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("이미지 파일이 필요합니다.", "IMAGE_REQUIRED"));
        }
        
        UploadResult uploadResult = fileStorageService.upload(image, FileType.PROFILE_IMAGE);
        String imageUrl = uploadResult.getUrl();
        
        // 프로필 이미지 URL 업데이트
        actorService.updateProfileImage(user, imageUrl);
        
        return ResponseEntity.ok(ApiResponse.success(Map.of("imageUrl", imageUrl)));
    }

    /**
     * 배우 프로필 등록
     */
    @Operation(summary = "배우 프로필 등록", description = "배우 회원가입 시 프로필을 등록합니다.")
    @PostMapping(value = "/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Map<String, Object>>> createProfile(
            @AuthenticationPrincipal User user,
            @Parameter(description = "활동명") @RequestParam("name") String name,
            @Parameter(description = "한 줄 소개") @RequestParam("introduction") String introduction,
            @Parameter(description = "나이대") @RequestParam("ageGroup") String ageGroup,
            @Parameter(description = "출생년도") @RequestParam(value = "birthYear", required = false) Integer birthYear,
            @Parameter(description = "특기 목록") @RequestParam(value = "skills", required = false) List<String> skills,
            @Parameter(description = "키 (cm)") @RequestParam(value = "height", required = false) Integer height,
            @Parameter(description = "몸무게 (kg)") @RequestParam(value = "weight", required = false) Integer weight,
            @Parameter(description = "프로필 이미지") @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {

        String profileImageUrl = null;
        if (profileImage != null && !profileImage.isEmpty()) {
            UploadResult uploadResult = fileStorageService.upload(profileImage, FileType.PROFILE_IMAGE);
            profileImageUrl = uploadResult.getUrl();
        }

        CreateActorProfileRequest request = CreateActorProfileRequest.builder()
                .name(name)
                .introduction(introduction)
                .ageGroup(ageGroup)
                .birthYear(birthYear)
                .skills(skills)
                .height(height)
                .weight(weight)
                .build();

        ActorDetailResponse profile = actorService.createProfile(user, request, profileImageUrl);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(Map.of(
                "actorId", profile.getId(),
                "name", profile.getStageName() != null ? profile.getStageName() : profile.getName(),
                "profileImageUrl", profile.getProfileImage() != null ? profile.getProfileImage() : ""
        )));
    }

    /**
     * AI 배우 추천
     */
    @Operation(summary = "AI 배우 추천", description = "프로젝트/캐릭터 정보 기반 AI 배우 추천")
    @PostMapping("/recommend")
    public ResponseEntity<ApiResponse<Map<String, List<ActorRecommendResponse>>>> recommendActors(
            @AuthenticationPrincipal User user,
            @RequestBody ActorRecommendRequest request) {

        List<ActorRecommendResponse> recommendations = actorService.recommendActors(user, request);
        return ResponseEntity.ok(ApiResponse.success(Map.of("recommendedActors", recommendations)));
    }

    /**
     * 포트폴리오 다운로드
     */
    @Operation(summary = "포트폴리오 다운로드", description = "배우 포트폴리오 PDF 다운로드 (현재 미구현)")
    @GetMapping("/{actorId}/portfolio")
    public ResponseEntity<ApiResponse<Map<String, String>>> downloadPortfolio(
            @Parameter(description = "배우 ID") @PathVariable UUID actorId) {

        // TODO: PDF 생성 로직 구현 필요
        // 현재는 플레이스홀더 응답
        return ResponseEntity.ok(ApiResponse.success(Map.of(
                "message", "포트폴리오 다운로드 기능은 추후 구현 예정입니다.",
                "actorId", actorId.toString()
        )));
    }

    /**
     * 배우에게 연락하기
     */
    @Operation(summary = "배우 연락하기", description = "에이전시가 배우에게 캐스팅 제안을 전송합니다.")
    @PostMapping("/{actorId}/contact")
    public ResponseEntity<ApiResponse<Map<String, Object>>> contactActor(
            @AuthenticationPrincipal User user,
            @Parameter(description = "배우 ID") @PathVariable UUID actorId,
            @Valid @RequestBody ContactActorRequest request) {

        UUID contactId = actorService.contactActor(user, actorId, request);
        return ResponseEntity.ok(ApiResponse.success(Map.of(
                "contactId", contactId,
                "status", "sent"
        )));
    }
}

