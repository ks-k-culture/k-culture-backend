package restapi.kculturebackend.infrastructure.storage;

import lombok.Builder;
import lombok.Getter;

/**
 * 파일 업로드 결과 DTO
 */
@Getter
@Builder
public class UploadResult {
    private String url;       // 접근 가능한 URL
    private String key;       // 저장소 내 고유 키
    private String filename;  // 원본 파일명
    private long size;        // 파일 크기 (bytes)
    private String mimeType;  // MIME 타입
}

