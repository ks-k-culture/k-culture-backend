package restapi.kculturebackend.domain.project.controller;

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
import restapi.kculturebackend.domain.project.dto.CreateProjectRequest;
import restapi.kculturebackend.domain.project.dto.ProjectResponse;
import restapi.kculturebackend.domain.project.dto.UpdateProjectRequest;
import restapi.kculturebackend.domain.project.entity.ProjectStatus;
import restapi.kculturebackend.domain.project.entity.ProjectType;
import restapi.kculturebackend.domain.project.service.ProjectService;
import restapi.kculturebackend.domain.user.entity.User;

import java.util.List;
import java.util.UUID;

/**
 * 프로젝트 API 컨트롤러
 */
@Tag(name = "Projects", description = "프로젝트 관련 API")
@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    /**
     * 프로젝트 목록 조회 (검색/필터)
     */
    @Operation(summary = "프로젝트 목록", description = "프로젝트 목록을 검색/필터하여 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<PaginationResponse<ProjectResponse>>> getProjects(
            @Parameter(description = "프로젝트명 검색") @RequestParam(required = false) String name,
            @Parameter(description = "프로젝트 유형") @RequestParam(required = false) ProjectType type,
            @Parameter(description = "프로젝트 상태") @RequestParam(required = false) ProjectStatus status,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<ProjectResponse> projects = projectService.getProjects(name, type, status, pageable);
        return ResponseEntity.ok(ApiResponse.success(PaginationResponse.from(projects)));
    }

    /**
     * 캐스팅 중인 프로젝트 목록
     */
    @Operation(summary = "캐스팅 중인 프로젝트", description = "현재 캐스팅 진행 중인 프로젝트 목록을 조회합니다.")
    @GetMapping("/casting")
    public ResponseEntity<ApiResponse<PaginationResponse<ProjectResponse>>> getCastingProjects(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<ProjectResponse> projects = projectService.getCastingProjects(pageable);
        return ResponseEntity.ok(ApiResponse.success(PaginationResponse.from(projects)));
    }

    /**
     * 내 프로젝트 목록
     */
    @Operation(summary = "내 프로젝트 목록", description = "로그인한 에이전시의 프로젝트 목록을 조회합니다.")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<ProjectResponse>>> getMyProjects(
            @AuthenticationPrincipal User user) {

        List<ProjectResponse> projects = projectService.getMyProjects(user);
        return ResponseEntity.ok(ApiResponse.success(projects));
    }

    /**
     * 프로젝트 상세 조회
     */
    @Operation(summary = "프로젝트 상세", description = "특정 프로젝트의 상세 정보를 조회합니다.")
    @GetMapping("/{projectId}")
    public ResponseEntity<ApiResponse<ProjectResponse>> getProject(
            @Parameter(description = "프로젝트 ID") @PathVariable UUID projectId) {

        ProjectResponse project = projectService.getProject(projectId);
        return ResponseEntity.ok(ApiResponse.success(project));
    }

    /**
     * 프로젝트 생성
     */
    @Operation(summary = "프로젝트 생성", description = "새로운 프로젝트를 등록합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<ProjectResponse>> createProject(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CreateProjectRequest request) {

        ProjectResponse project = projectService.createProject(user, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(project));
    }

    /**
     * 프로젝트 수정
     */
    @Operation(summary = "프로젝트 수정", description = "프로젝트 정보를 수정합니다.")
    @PutMapping("/{projectId}")
    public ResponseEntity<ApiResponse<ProjectResponse>> updateProject(
            @AuthenticationPrincipal User user,
            @Parameter(description = "프로젝트 ID") @PathVariable UUID projectId,
            @Valid @RequestBody UpdateProjectRequest request) {

        ProjectResponse project = projectService.updateProject(user, projectId, request);
        return ResponseEntity.ok(ApiResponse.success(project));
    }

    /**
     * 프로젝트 삭제
     */
    @Operation(summary = "프로젝트 삭제", description = "프로젝트를 삭제합니다.")
    @DeleteMapping("/{projectId}")
    public ResponseEntity<ApiResponse<Void>> deleteProject(
            @AuthenticationPrincipal User user,
            @Parameter(description = "프로젝트 ID") @PathVariable UUID projectId) {

        projectService.deleteProject(user, projectId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}

