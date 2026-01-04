package restapi.kculturebackend.common.exception;

/**
 * 인증 실패 예외 (401)
 */
public class UnauthorizedException extends BusinessException {
    
    public UnauthorizedException() {
        super(ErrorCode.UNAUTHORIZED);
    }

    public UnauthorizedException(ErrorCode errorCode) {
        super(errorCode);
    }

    public UnauthorizedException(String customMessage) {
        super(ErrorCode.UNAUTHORIZED, customMessage);
    }
}
