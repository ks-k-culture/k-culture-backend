package restapi.kculturebackend.domain.job.controller;

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
import restapi.kculturebackend.domain.job.dto.*;
import restapi.kculturebackend.domain.job.entity.JobCategory;
import restapi.kculturebackend.domain.job.service.JobService;
import restapi.kculturebackend.domain.user.entity.User;

import java.util.Map;
import java.util.UUID;

/**
 * 작품구인 API 컨트롤러
 */
@Tag(name = "Jobs", description = "작품구인 관련 API (CRUD)")
@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    /**
     * 작품구인 목록 조회
     */
    @Operation(summary = "작품구인 목록 조회", description = "필터 조건으로 작품구인 목록을 검색합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<PaginationResponse<JobSummaryResponse>>> getJobs(
            @Parameter(description = "카테고리") @RequestParam(required = false) JobCategory category,
            @Parameter(description = "성별") @RequestParam(required = false) String gender,
            @Parameter(description = "나이대") @RequestParam(required = false) String ageRange,
            @Parameter(description = "품앗이 여부") @RequestParam(required = false) Boolean isPumasi,
            @Parameter(description = "상태 (all, 모집중, 마감됨)") @RequestParam(required = false, defaultValue = "all") String status,
            @Parameter(description = "검색어") @RequestParam(required = false) String search,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<JobSummaryResponse> jobs = jobService.getJobs(category, gender, ageRange, isPumasi, status, search, pageable);
        return ResponseEntity.ok(ApiResponse.success(PaginationResponse.from(jobs)));
    }

    /**
     * 작품구인 상세 조회
     */
    @Operation(summary = "작품구인 상세 조회", description = "작품구인 공고 상세 정보를 조회합니다.")
    @GetMapping("/{jobId}")
    public ResponseEntity<ApiResponse<JobDetailResponse>> getJob(
            @Parameter(description = "작품구인 ID") @PathVariable UUID jobId) {

        JobDetailResponse job = jobService.getJob(jobId);
        return ResponseEntity.ok(ApiResponse.success(job));
    }

    /**
     * 작품구인 등록
     */
    @Operation(summary = "작품구인 등록", description = "새 작품구인 공고를 등록합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> createJob(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CreateJobRequest request) {

        JobDetailResponse job = jobService.createJob(user, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(Map.of(
                "id", job.getId(),
                "title", job.getTitle()
        )));
    }

    /**
     * 작품구인 수정
     */
    @Operation(summary = "작품구인 수정", description = "작품구인 공고를 수정합니다.")
    @PutMapping("/{jobId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateJob(
            @AuthenticationPrincipal User user,
            @Parameter(description = "작품구인 ID") @PathVariable UUID jobId,
            @Valid @RequestBody UpdateJobRequest request) {

        JobDetailResponse job = jobService.updateJob(user, jobId, request);
        return ResponseEntity.ok(ApiResponse.success(Map.of(
                "id", job.getId(),
                "title", job.getTitle()
        )));
    }

    /**
     * 작품구인 삭제
     */
    @Operation(summary = "작품구인 삭제", description = "작품구인 공고를 삭제합니다.")
    @DeleteMapping("/{jobId}")
    public ResponseEntity<ApiResponse<Map<String, String>>> deleteJob(
            @AuthenticationPrincipal User user,
            @Parameter(description = "작품구인 ID") @PathVariable UUID jobId) {

        jobService.deleteJob(user, jobId);
        return ResponseEntity.ok(ApiResponse.success(Map.of("message", "작품구인 공고가 삭제되었습니다.")));
    }
}
