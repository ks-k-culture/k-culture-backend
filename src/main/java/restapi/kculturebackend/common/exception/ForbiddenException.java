package restapi.kculturebackend.common.exception;

/**
 * 권한 없음 예외 (403)
 */
public class ForbiddenException extends BusinessException {
    
    public ForbiddenException() {
        super(ErrorCode.FORBIDDEN);
    }

    public ForbiddenException(String customMessage) {
        super(ErrorCode.FORBIDDEN, customMessage);
    }

    public ForbiddenException(ErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }
}
