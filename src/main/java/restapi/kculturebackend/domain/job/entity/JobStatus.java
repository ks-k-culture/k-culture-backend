package restapi.kculturebackend.domain.job.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

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

    @JsonValue
    public String getDisplayName() {
        return displayName;
    }

    @JsonCreator
    public static JobStatus fromDisplayName(String displayName) {
        for (JobStatus status : values()) {
            if (status.displayName.equals(displayName)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown status: " + displayName);
    }
}
