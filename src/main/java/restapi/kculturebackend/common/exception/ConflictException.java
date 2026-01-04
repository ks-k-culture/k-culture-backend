package restapi.kculturebackend.common.exception;

/**
 * 중복/충돌 예외 (409)
 */
public class ConflictException extends BusinessException {
    
    public ConflictException() {
        super(ErrorCode.CONFLICT);
    }

    public ConflictException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ConflictException(String customMessage) {
        super(ErrorCode.CONFLICT, customMessage);
    }
}
