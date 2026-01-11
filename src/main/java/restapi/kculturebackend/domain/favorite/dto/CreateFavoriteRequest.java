package restapi.kculturebackend.domain.favorite.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import restapi.kculturebackend.domain.favorite.entity.FavoriteType;

import java.util.UUID;

/**
 * 찜 추가 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateFavoriteRequest {

    @NotNull(message = "찜 타입은 필수입니다.")
    private FavoriteType type;

    @NotNull(message = "대상 ID는 필수입니다.")
    private UUID targetId;
}
