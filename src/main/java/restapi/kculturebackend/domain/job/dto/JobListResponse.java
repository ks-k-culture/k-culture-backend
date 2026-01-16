package restapi.kculturebackend.domain.job.dto;

import java.util.List;

import org.springframework.data.domain.Page;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import restapi.kculturebackend.common.dto.Pagination;

/**
 * 작품구인 목록 응답 (OpenAPI 스펙에 맞춤)
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class JobListResponse {
    private List<JobSummaryResponse> jobs;
    private Pagination pagination;

    public static JobListResponse from(Page<JobSummaryResponse> page) {
        return new JobListResponse(
                page.getContent(),
                Pagination.from(page)
        );
    }
}
