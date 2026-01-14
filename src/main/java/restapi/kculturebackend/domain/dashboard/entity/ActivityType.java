package restapi.kculturebackend.domain.dashboard.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 활동 타입
 */
@Getter
@RequiredArgsConstructor
public enum ActivityType {
    PROFILE_VIEW("view", "프로필 조회"),
    FAVORITE("like", "찜"),
    CONTACT_REQUEST("contact", "섭외 요청");

    private final String code;
    private final String displayName;
}
