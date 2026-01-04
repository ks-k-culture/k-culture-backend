package restapi.kculturebackend.domain.actor.entity;

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
    CAMEO("카메오"),
    EXTRA("엑스트라"),
    VOICE("성우"),
    OTHER("기타");

    private final String displayName;
}

