package restapi.kculturebackend.domain.dashboard.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 최근 활동 내역 목록 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecentActivitiesResponse {
    private List<ActivityResponse> activities;
    private int total;

    public static RecentActivitiesResponse of(List<ActivityResponse> activities, int total) {
        return RecentActivitiesResponse.builder()
                .activities(activities)
                .total(total)
                .build();
    }
}
