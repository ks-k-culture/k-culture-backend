package restapi.kculturebackend.domain.favorite.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 찜 타입
 */
@Getter
@RequiredArgsConstructor
public enum FavoriteType {
    ACTOR("actor", "배우"),
    PROJECT("project", "프로젝트");

    private final String code;
    private final String displayName;
}
