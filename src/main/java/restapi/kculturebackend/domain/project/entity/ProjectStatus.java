package restapi.kculturebackend.domain.project.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 프로젝트 상태
 */
@Getter
@RequiredArgsConstructor
public enum ProjectStatus {
    PLANNING("기획중"),
    CASTING("캐스팅중"),
    PRE_PRODUCTION("프리프로덕션"),
    IN_PRODUCTION("촬영중"),
    POST_PRODUCTION("후반작업"),
    COMPLETED("완료"),
    RELEASED("공개됨"),
    CANCELLED("취소됨");

    private final String displayName;
}

