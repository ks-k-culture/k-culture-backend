package restapi.kculturebackend.domain.favorite.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

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

    @JsonValue
    public String getCode() {
        return code;
    }

    @JsonCreator
    public static FavoriteType fromCode(String code) {
        for (FavoriteType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown FavoriteType code: " + code);
    }
}
