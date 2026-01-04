package restapi.kculturebackend.common.exception;

/**
 * 유효성 검사 실패 예외 (400)
 */
public class ValidationException extends BusinessException {
    
    public ValidationException() {
        super(ErrorCode.VALIDATION_ERROR);
    }

    public ValidationException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ValidationException(String customMessage) {
        super(ErrorCode.VALIDATION_ERROR, customMessage);
    }
}
