package restapi.kculturebackend.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 표준화된 에러 코드 Enum
 * 
 * 코드 규칙:
 * - AUTH_xxx: 인증 관련
 * - USER_xxx: 사용자 관련
 * - ACTOR_xxx: 배우 관련
 * - PROJECT_xxx: 프로젝트 관련
 * - JOB_xxx: 작품구인 관련
 * - FILE_xxx: 파일 관련
 * - COMMON_xxx: 공통
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // ===== 인증 관련 (AUTH_xxx) =====
    UNAUTHORIZED("AUTH_001", "인증이 필요합니다.", HttpStatus.UNAUTHORIZED),
    INVALID_CREDENTIALS("AUTH_002", "이메일 또는 비밀번호가 올바르지 않습니다.", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN("AUTH_003", "유효하지 않거나 만료된 토큰입니다.", HttpStatus.UNAUTHORIZED),
    EXPIRED_TOKEN("AUTH_004", "토큰이 만료되었습니다.", HttpStatus.UNAUTHORIZED),
    INVALID_REFRESH_TOKEN("AUTH_005", "유효하지 않은 Refresh Token입니다.", HttpStatus.UNAUTHORIZED),

    // ===== 사용자 관련 (USER_xxx) =====
    USER_NOT_FOUND("USER_001", "사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    EMAIL_ALREADY_EXISTS("USER_002", "이미 사용 중인 이메일입니다.", HttpStatus.CONFLICT),
    PASSWORD_MISMATCH("USER_003", "비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
    TERMS_NOT_AGREED("USER_004", "필수 약관에 동의해야 합니다.", HttpStatus.BAD_REQUEST),
    USER_PROFILE_NOT_FOUND("USER_005", "사용자 프로필을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    // ===== 배우 관련 (ACTOR_xxx) =====
    ACTOR_NOT_FOUND("ACTOR_001", "배우를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    ACTOR_PROFILE_NOT_FOUND("ACTOR_002", "배우 프로필을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    FILMOGRAPHY_NOT_FOUND("ACTOR_003", "필모그래피를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    SHOWREEL_NOT_FOUND("ACTOR_004", "쇼릴을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    // ===== 프로젝트 관련 (PROJECT_xxx) =====
    PROJECT_NOT_FOUND("PROJECT_001", "프로젝트를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    CHARACTER_NOT_FOUND("PROJECT_002", "캐릭터를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    // ===== 에이전시 관련 (AGENCY_xxx) =====
    AGENCY_NOT_FOUND("AGENCY_001", "에이전시를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    AGENCY_PROFILE_NOT_FOUND("AGENCY_002", "에이전시 프로필을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    // ===== 작품구인 관련 (JOB_xxx) =====
    JOB_NOT_FOUND("JOB_001", "작품구인을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    // ===== 찜 관련 (FAVORITE_xxx) =====
    FAVORITE_NOT_FOUND("FAVORITE_001", "찜 항목을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    FAVORITE_ALREADY_EXISTS("FAVORITE_002", "이미 찜한 항목입니다.", HttpStatus.CONFLICT),

    // ===== 공지사항 관련 (NOTICE_xxx) =====
    NOTICE_NOT_FOUND("NOTICE_001", "공지사항을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    // ===== 알림 관련 (NOTIFICATION_xxx) =====
    NOTIFICATION_NOT_FOUND("NOTIFICATION_001", "알림을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    // ===== 파일 관련 (FILE_xxx) =====
    FILE_UPLOAD_FAILED("FILE_001", "파일 업로드에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_FILE_TYPE("FILE_002", "지원하지 않는 파일 형식입니다.", HttpStatus.BAD_REQUEST),
    FILE_SIZE_EXCEEDED("FILE_003", "파일 크기가 제한을 초과했습니다.", HttpStatus.BAD_REQUEST),
    FILE_NOT_FOUND("FILE_004", "파일을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    // ===== 공통 (COMMON_xxx) =====
    VALIDATION_ERROR("COMMON_001", "입력값이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
    FORBIDDEN("COMMON_002", "접근 권한이 없습니다.", HttpStatus.FORBIDDEN),
    RESOURCE_NOT_FOUND("COMMON_003", "요청한 리소스를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    CONFLICT("COMMON_004", "리소스 충돌이 발생했습니다.", HttpStatus.CONFLICT),
    INTERNAL_SERVER_ERROR("COMMON_999", "서버 내부 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String message;
    private final HttpStatus status;
}

