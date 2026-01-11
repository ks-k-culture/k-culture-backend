package restapi.kculturebackend.domain.notification.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 알림 타입
 */
@Getter
@RequiredArgsConstructor
public enum NotificationType {
    CASTING_REQUEST("casting_request", "캐스팅 요청"),
    PROFILE_VIEW("profile_view", "프로필 조회"),
    FAVORITE("favorite", "찜"),
    MESSAGE("message", "메시지"),
    SYSTEM("system", "시스템");

    private final String code;
    private final String displayName;
}
