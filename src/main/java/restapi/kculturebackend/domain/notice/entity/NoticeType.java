package restapi.kculturebackend.domain.notice.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 공지사항 유형
 */
@Getter
@RequiredArgsConstructor
public enum NoticeType {
    GENERAL("일반공지"),
    UPDATE("업데이트"),
    EVENT("이벤트"),
    MAINTENANCE("점검");

    private final String displayName;
}
