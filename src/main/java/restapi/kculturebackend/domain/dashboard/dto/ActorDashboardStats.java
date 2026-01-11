package restapi.kculturebackend.domain.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 배우 대시보드 통계 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActorDashboardStats {
    private Integer profileViews;
    private Integer likes;
    private Integer contactRequests;
    private Integer profileCompleteness;
}
