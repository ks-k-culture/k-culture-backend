package restapi.kculturebackend.common.exception;

/**
 * 리소스 없음 예외 (404)
 */
public class NotFoundException extends BusinessException {
    
    public NotFoundException() {
        super(ErrorCode.RESOURCE_NOT_FOUND);
    }

    public NotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }

    public NotFoundException(String customMessage) {
        super(ErrorCode.RESOURCE_NOT_FOUND, customMessage);
    }

    /**
     * 리소스 타입을 기반으로 메시지 생성
     */
    public static NotFoundException forResource(String resource) {
        return new NotFoundException(resource + "을(를) 찾을 수 없습니다.");
    }
}
