package restapi.kculturebackend.common.exception;

/**
 * 유효성 검사 실패 예외 (400)
 */
public class ValidationException extends BusinessException {
    public ValidationException(String message) {
        super("VALIDATION_ERROR", message);
    }

    public ValidationException(String code, String message) {
        super(code, message);
    }

    public ValidationException() {
        super("VALIDATION_ERROR", "입력값이 올바르지 않습니다.");
    }
}

