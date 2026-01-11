package restapi.kculturebackend.domain.actor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * AI 배우 추천 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActorRecommendRequest {
    private UUID projectId;
    private UUID characterId;
    private String synopsis;
    private ActorRecommendFilters filters;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ActorRecommendFilters {
        private String ageRange;
        private String gender;
        private String roleType;
    }
}
