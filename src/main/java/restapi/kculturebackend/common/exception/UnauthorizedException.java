package restapi.kculturebackend.common.exception;

/**
 * 인증 실패 예외 (401)
 */
public class UnauthorizedException extends BusinessException {
    public UnauthorizedException(String message) {
        super("UNAUTHORIZED", message);
    }

    public UnauthorizedException() {
        super("UNAUTHORIZED", "인증이 필요합니다.");
    }
}

