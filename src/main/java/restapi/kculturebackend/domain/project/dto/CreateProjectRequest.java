package restapi.kculturebackend.domain.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import restapi.kculturebackend.domain.project.entity.ProjectType;

import java.time.LocalDate;

/**
 * 프로젝트 생성 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProjectRequest {

    @NotBlank(message = "프로젝트명은 필수입니다.")
    @Size(max = 200, message = "프로젝트명은 200자 이내여야 합니다.")
    private String projectName;

    @Size(max = 100, message = "제작사명은 100자 이내여야 합니다.")
    private String company;

    @NotNull(message = "프로젝트 유형은 필수입니다.")
    private ProjectType projectType;

    @Size(max = 50, message = "장르는 50자 이내여야 합니다.")
    private String genre;

    @Size(max = 2000, message = "프로젝트 설명은 2000자 이내여야 합니다.")
    private String description;

    @Size(max = 100, message = "감독명은 100자 이내여야 합니다.")
    private String director;

    @Size(max = 100, message = "작가명은 100자 이내여야 합니다.")
    private String writer;

    @Size(max = 500, message = "썸네일 URL은 500자 이내여야 합니다.")
    private String thumbnailUrl;

    private LocalDate startDate;
    private LocalDate endDate;

    @Size(max = 200, message = "촬영 장소는 200자 이내여야 합니다.")
    private String filmingLocation;
}

