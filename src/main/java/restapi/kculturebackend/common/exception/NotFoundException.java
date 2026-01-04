package restapi.kculturebackend.common.exception;

/**
 * 리소스 없음 예외 (404)
 */
public class NotFoundException extends BusinessException {
    public NotFoundException(String message) {
        super("NOT_FOUND", message);
    }

    public NotFoundException(String resource) {
        super("NOT_FOUND", resource + "을(를) 찾을 수 없습니다.");
    }
}

