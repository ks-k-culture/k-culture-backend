package restapi.kculturebackend.domain.job.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 작품구인 상태
 */
@Getter
@RequiredArgsConstructor
public enum JobStatus {
    RECRUITING("모집중"),
    CLOSED("마감됨");

    private final String displayName;
}
