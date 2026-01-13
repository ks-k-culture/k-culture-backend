package restapi.kculturebackend.domain.project.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import restapi.kculturebackend.common.exception.ErrorCode;
import restapi.kculturebackend.common.exception.ForbiddenException;
import restapi.kculturebackend.common.exception.NotFoundException;
import restapi.kculturebackend.domain.agency.entity.AgencyProfile;
import restapi.kculturebackend.domain.agency.repository.AgencyProfileRepository;
import restapi.kculturebackend.domain.project.dto.CreateProjectRequest;
import restapi.kculturebackend.domain.project.dto.ProjectResponse;
import restapi.kculturebackend.domain.project.dto.UpdateProjectRequest;
import restapi.kculturebackend.domain.project.entity.Project;
import restapi.kculturebackend.domain.project.entity.ProjectStatus;
import restapi.kculturebackend.domain.project.entity.ProjectType;
import restapi.kculturebackend.domain.project.repository.ProjectRepository;
import restapi.kculturebackend.domain.user.entity.User;
import restapi.kculturebackend.domain.user.entity.UserType;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 프로젝트 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final AgencyProfileRepository agencyProfileRepository;

    /**
     * 프로젝트 목록 조회 (검색/필터)
     */
    @Transactional(readOnly = true)
    public Page<ProjectResponse> getProjects(String name, ProjectType type, ProjectStatus status, Pageable pageable) {
        String typeStr = type != null ? type.name() : null;
        String statusStr = status != null ? status.name() : null;
        return projectRepository.search(name, typeStr, statusStr, pageable)
                .map(ProjectResponse::from);
    }

    /**
     * 캐스팅 중인 프로젝트 목록
     */
    @Transactional(readOnly = true)
    public Page<ProjectResponse> getCastingProjects(Pageable pageable) {
        return projectRepository.findCastingProjects(pageable)
                .map(ProjectResponse::from);
    }

    /**
     * 프로젝트 상세 조회
     */
    @Transactional(readOnly = true)
    public ProjectResponse getProject(UUID projectId) {
        Project project = projectRepository.findByIdWithAgency(projectId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.PROJECT_NOT_FOUND));

        return ProjectResponse.from(project);
    }

    /**
     * 내 프로젝트 목록 조회
     */
    @Transactional(readOnly = true)
    public List<ProjectResponse> getMyProjects(User user) {
        validateAgencyUser(user);

        return projectRepository.findByAgencyId(user.getId()).stream()
                .map(ProjectResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 프로젝트 생성
     */
    @Transactional
    public ProjectResponse createProject(User user, CreateProjectRequest request) {
        validateAgencyUser(user);

        AgencyProfile agency = agencyProfileRepository.findById(user.getId())
                .orElseThrow(() -> new NotFoundException(ErrorCode.AGENCY_PROFILE_NOT_FOUND));

        Project project = Project.create(
                agency,
                request.getProjectName(),
                request.getCompany(),
                request.getProjectType(),
                request.getGenre(),
                request.getDescription(),
                request.getDirector(),
                request.getWriter(),
                request.getThumbnailUrl(),
                request.getStartDate(),
                request.getEndDate(),
                request.getFilmingLocation()
        );

        Project saved = projectRepository.save(project);
        log.info("Project created: {} by agency: {}", saved.getId(), user.getId());

        return ProjectResponse.from(saved);
    }

    /**
     * 프로젝트 수정
     */
    @Transactional
    public ProjectResponse updateProject(User user, UUID projectId, UpdateProjectRequest request) {
        validateAgencyUser(user);

        Project project = projectRepository.findByIdWithAgency(projectId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.PROJECT_NOT_FOUND));

        // 본인의 프로젝트인지 확인
        if (!project.getAgency().getUserId().equals(user.getId())) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN, "본인의 프로젝트만 수정할 수 있습니다.");
        }

        project.update(
                request.getProjectName(),
                request.getCompany(),
                request.getProjectType(),
                request.getGenre(),
                request.getStatus(),
                request.getDescription(),
                request.getDirector(),
                request.getWriter(),
                request.getThumbnailUrl(),
                request.getStartDate(),
                request.getEndDate(),
                request.getFilmingLocation()
        );

        Project saved = projectRepository.save(project);
        log.info("Project updated: {}", projectId);

        return ProjectResponse.from(saved);
    }

    /**
     * 프로젝트 삭제
     */
    @Transactional
    public void deleteProject(User user, UUID projectId) {
        validateAgencyUser(user);

        Project project = projectRepository.findByIdWithAgency(projectId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.PROJECT_NOT_FOUND));

        // 본인의 프로젝트인지 확인
        if (!project.getAgency().getUserId().equals(user.getId())) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN, "본인의 프로젝트만 삭제할 수 있습니다.");
        }

        projectRepository.delete(project);
        log.info("Project deleted: {}", projectId);
    }

    /**
     * 사용자가 에이전시 타입인지 검증
     */
    private void validateAgencyUser(User user) {
        if (user.getType() != UserType.AGENCY) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN, "에이전시 계정만 접근할 수 있습니다.");
        }
    }
}

