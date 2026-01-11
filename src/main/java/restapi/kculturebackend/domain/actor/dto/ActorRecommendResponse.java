package restapi.kculturebackend.domain.actor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import restapi.kculturebackend.domain.actor.entity.ActorProfile;

import java.util.List;
import java.util.UUID;

/**
 * AI 배우 추천 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActorRecommendResponse {
    private UUID id;
    private String name;
    private String imageUrl;
    private Integer matchScore;
    private List<String> matchReasons;

    public static ActorRecommendResponse from(ActorProfile actor, int matchScore, List<String> matchReasons) {
        return ActorRecommendResponse.builder()
                .id(actor.getUserId())
                .name(actor.getStageName() != null ? actor.getStageName() : actor.getUser().getName())
                .imageUrl(actor.getUser().getProfileImage())
                .matchScore(matchScore)
                .matchReasons(matchReasons)
                .build();
    }
}
