package restapi.kculturebackend.domain.actor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import restapi.kculturebackend.domain.actor.entity.Filmography;
import restapi.kculturebackend.domain.actor.entity.FilmographyType;
import restapi.kculturebackend.domain.actor.entity.RoleType;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 필모그래피 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FilmographyResponse {
    private UUID id;
    private UUID actorId;
    private String title;
    private Integer year;
    private FilmographyType type;
    private String typeDisplayName;
    private String role;
    private RoleType roleType;
    private String roleTypeDisplayName;
    private String thumbnailUrl;
    private String description;
    private String director;
    private String production;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static FilmographyResponse from(Filmography filmography) {
        return FilmographyResponse.builder()
                .id(filmography.getId())
                .actorId(filmography.getActor().getUserId())
                .title(filmography.getTitle())
                .year(filmography.getYear())
                .type(filmography.getType())
                .typeDisplayName(filmography.getType().getDisplayName())
                .role(filmography.getRole())
                .roleType(filmography.getRoleType())
                .roleTypeDisplayName(filmography.getRoleType() != null ? filmography.getRoleType().getDisplayName() : null)
                .thumbnailUrl(filmography.getThumbnailUrl())
                .description(filmography.getDescription())
                .director(filmography.getDirector())
                .production(filmography.getProduction())
                .createdAt(filmography.getCreatedAt())
                .updatedAt(filmography.getUpdatedAt())
                .build();
    }
}

