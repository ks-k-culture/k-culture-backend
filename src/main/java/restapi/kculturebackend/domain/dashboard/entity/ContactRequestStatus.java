package restapi.kculturebackend.domain.dashboard.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 섭외 요청 상태
 */
@Getter
@RequiredArgsConstructor
public enum ContactRequestStatus {
    PENDING("pending", "대기중"),
    ACCEPTED("accepted", "수락"),
    REJECTED("rejected", "거절"),
    CANCELLED("cancelled", "취소");

    private final String code;
    private final String displayName;
}
