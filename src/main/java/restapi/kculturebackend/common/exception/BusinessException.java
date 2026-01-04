package restapi.kculturebackend.common.exception;

import lombok.Getter;

/**
 * 비즈니스 로직 예외의 최상위 클래스
 */
@Getter
public class BusinessException extends RuntimeException {
    private final String code;

    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }
}

