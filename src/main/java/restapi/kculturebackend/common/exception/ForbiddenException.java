package restapi.kculturebackend.common.exception;

/**
 * 권한 없음 예외 (403)
 */
public class ForbiddenException extends BusinessException {
    public ForbiddenException(String message) {
        super("FORBIDDEN", message);
    }

    public ForbiddenException() {
        super("FORBIDDEN", "접근 권한이 없습니다.");
    }
}

