package restapi.kculturebackend.domain.actor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import restapi.kculturebackend.domain.actor.entity.FilmographyType;
import restapi.kculturebackend.domain.actor.entity.RoleType;

/**
 * 필모그래피 수정 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateFilmographyRequest {

    @NotBlank(message = "작품 제목은 필수입니다.")
    @Size(max = 200, message = "작품 제목은 200자 이내여야 합니다.")
    private String title;

    private Integer year;

    @NotNull(message = "작품 유형은 필수입니다.")
    private FilmographyType type;

    @Size(max = 100, message = "배역명은 100자 이내여야 합니다.")
    private String role;

    private RoleType roleType;

    @Size(max = 500, message = "썸네일 URL은 500자 이내여야 합니다.")
    private String thumbnailUrl;

    @Size(max = 500, message = "설명은 500자 이내여야 합니다.")
    private String description;

    @Size(max = 100, message = "감독명은 100자 이내여야 합니다.")
    private String director;

    @Size(max = 100, message = "제작사/방송사명은 100자 이내여야 합니다.")
    private String production;
}

