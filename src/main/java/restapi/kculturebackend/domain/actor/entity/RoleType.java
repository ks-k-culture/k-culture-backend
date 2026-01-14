package restapi.kculturebackend.domain.actor.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 배역 유형
 */
@Getter
@RequiredArgsConstructor
public enum RoleType {
    LEAD("주연"),
    SUPPORTING("조연"),
    MINOR("단역"),
    CAMEO("특별출연"),
    EXTRA("엑스트라"),
    VOICE("성우"),
    OTHER("기타");

    private final String displayName;

    @JsonValue
    public String getDisplayName() {
        return displayName;
    }

    @JsonCreator
    public static RoleType fromDisplayName(String displayName) {
        for (RoleType type : values()) {
            if (type.displayName.equals(displayName)) {
                return type;
            }
        }
        try {
            return RoleType.valueOf(displayName.toUpperCase());
        } catch (IllegalArgumentException e) {
            return OTHER;
        }
    }
}

