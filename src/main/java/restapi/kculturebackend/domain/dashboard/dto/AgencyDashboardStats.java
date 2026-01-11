package restapi.kculturebackend.domain.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 에이전시 대시보드 통계 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgencyDashboardStats {
    private Integer activeProjects;
    private Integer favoriteActors;
    private Integer sentContacts;
    private Integer totalCharacters;
}
