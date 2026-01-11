package restapi.kculturebackend.domain.job.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import restapi.kculturebackend.domain.job.entity.Job;
import restapi.kculturebackend.domain.job.entity.JobCategory;
import restapi.kculturebackend.domain.job.entity.JobStatus;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 작품구인 상세 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobDetailResponse {
    private UUID id;
    private JobCategory category;
    private String categoryDisplayName;
    private Boolean isPumasi;
    private Integer price;
    private String title;
    private String description;
    private String gender;
    private String ageRange;
    private String production;
    private String workTitle;
    private String shootingDate;
    private String shootingLocation;
    private JobStatus status;
    private String statusDisplayName;
    private Integer views;
    private String contactEmail;
    private String contactPhone;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static JobDetailResponse from(Job job) {
        return JobDetailResponse.builder()
                .id(job.getId())
                .category(job.getCategory())
                .categoryDisplayName(job.getCategory().getDisplayName())
                .isPumasi(job.getIsPumasi())
                .price(job.getPrice())
                .title(job.getTitle())
                .description(job.getDescription())
                .gender(job.getGender())
                .ageRange(job.getAgeRange())
                .production(job.getProduction())
                .workTitle(job.getWorkTitle())
                .shootingDate(job.getShootingDate())
                .shootingLocation(job.getShootingLocation())
                .status(job.getStatus())
                .statusDisplayName(job.getStatus().getDisplayName())
                .views(job.getViews())
                .contactEmail(job.getContactEmail())
                .contactPhone(job.getContactPhone())
                .createdAt(job.getCreatedAt())
                .updatedAt(job.getUpdatedAt())
                .build();
    }
}
