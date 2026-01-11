package restapi.kculturebackend.domain.favorite.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import restapi.kculturebackend.domain.actor.dto.ActorSummaryResponse;
import restapi.kculturebackend.domain.favorite.entity.Favorite;
import restapi.kculturebackend.domain.favorite.entity.FavoriteType;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 찜 목록 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoriteResponse {
    private UUID id;
    private FavoriteType type;
    private UUID targetId;
    private ActorSummaryResponse actor;
    private LocalDateTime createdAt;

    public static FavoriteResponse from(Favorite favorite) {
        return FavoriteResponse.builder()
                .id(favorite.getId())
                .type(favorite.getType())
                .targetId(favorite.getTargetId())
                .createdAt(favorite.getCreatedAt())
                .build();
    }

    public static FavoriteResponse from(Favorite favorite, ActorSummaryResponse actor) {
        return FavoriteResponse.builder()
                .id(favorite.getId())
                .type(favorite.getType())
                .targetId(favorite.getTargetId())
                .actor(actor)
                .createdAt(favorite.getCreatedAt())
                .build();
    }
}
