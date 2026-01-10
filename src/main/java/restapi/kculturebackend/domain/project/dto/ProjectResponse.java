package restapi.kculturebackend.domain.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import restapi.kculturebackend.domain.project.entity.Project;
import restapi.kculturebackend.domain.project.entity.ProjectStatus;
import restapi.kculturebackend.domain.project.entity.ProjectType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 프로젝트 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectResponse {
    private UUID id;
    private UUID agencyId;
    private String agencyName;
    private String projectName;
    private String company;
    private ProjectType projectType;
    private String projectTypeDisplayName;
    private String genre;
    private ProjectStatus status;
    private String statusDisplayName;
    private String description;
    private String director;
    private String writer;
    private String thumbnailUrl;
    private LocalDate startDate;
    private LocalDate endDate;
    private String filmingLocation;
    private int characterCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ProjectResponse from(Project project) {
        return ProjectResponse.builder()
                .id(project.getId())
                .agencyId(project.getAgency().getUserId())
                .agencyName(project.getAgency().getAgencyName())
                .projectName(project.getProjectName())
                .company(project.getCompany())
                .projectType(project.getProjectType())
                .projectTypeDisplayName(project.getProjectType().getDisplayName())
                .genre(project.getGenre())
                .status(project.getStatus())
                .statusDisplayName(project.getStatus().getDisplayName())
                .description(project.getDescription())
                .director(project.getDirector())
                .writer(project.getWriter())
                .thumbnailUrl(project.getThumbnailUrl())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .filmingLocation(project.getFilmingLocation())
                .characterCount(project.getCharacters() != null ? project.getCharacters().size() : 0)
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .build();
    }
}

