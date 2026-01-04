package restapi.kculturebackend.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 비즈니스 로직 예외의 최상위 클래스
 * ErrorCode Enum 기반으로 일관된 에러 처리
 */
@Getter
public class BusinessException extends RuntimeException {
    
    private final ErrorCode errorCode;
    private final String customMessage;

    /**
     * ErrorCode 기반 생성자 (권장)
     */
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.customMessage = null;
    }

    /**
     * ErrorCode + 커스텀 메시지 생성자
     */
    public BusinessException(ErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
        this.customMessage = customMessage;
    }

    /**
     * ErrorCode + 원인 예외 생성자
     */
    public BusinessException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
        this.customMessage = null;
    }

    /**
     * 에러 코드 문자열 반환
     */
    public String getCode() {
        return errorCode.getCode();
    }

    /**
     * HTTP 상태 코드 반환
     */
    public HttpStatus getStatus() {
        return errorCode.getStatus();
    }

    /**
     * 최종 메시지 반환 (커스텀 메시지가 있으면 커스텀, 없으면 기본)
     */
    public String getFinalMessage() {
        return customMessage != null ? customMessage : errorCode.getMessage();
    }
}

