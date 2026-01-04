package restapi.kculturebackend.common.exception;

/**
 * 중복/충돌 예외 (409)
 */
public class ConflictException extends BusinessException {
    public ConflictException(String code, String message) {
        super(code, message);
    }

    public ConflictException(String message) {
        super("CONFLICT", message);
    }
}

