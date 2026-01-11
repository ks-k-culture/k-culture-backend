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
 * 작품구인 요약 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobSummaryResponse {
    private UUID id;
    private JobCategory category;
    private String categoryDisplayName;
    private Boolean isPumasi;
    private Integer price;
    private String title;
    private String gender;
    private String production;
    private String workTitle;
    private JobStatus status;
    private String statusDisplayName;
    private Integer views;
    private LocalDateTime createdAt;

    public static JobSummaryResponse from(Job job) {
        return JobSummaryResponse.builder()
                .id(job.getId())
                .category(job.getCategory())
                .categoryDisplayName(job.getCategory().getDisplayName())
                .isPumasi(job.getIsPumasi())
                .price(job.getPrice())
                .title(job.getTitle())
                .gender(job.getGender())
                .production(job.getProduction())
                .workTitle(job.getWorkTitle())
                .status(job.getStatus())
                .statusDisplayName(job.getStatus().getDisplayName())
                .views(job.getViews())
                .createdAt(job.getCreatedAt())
                .build();
    }
}
