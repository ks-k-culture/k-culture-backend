package restapi.kculturebackend.domain.project.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 성별
 */
@Getter
@RequiredArgsConstructor
public enum Gender {
    MALE("남성"),
    FEMALE("여성"),
    ANY("무관");

    private final String displayName;
}

