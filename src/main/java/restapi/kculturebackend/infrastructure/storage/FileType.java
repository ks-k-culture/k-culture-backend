package restapi.kculturebackend.infrastructure.storage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 파일 타입별 저장 경로 정의
 */
@Getter
@RequiredArgsConstructor
public enum FileType {
    PROFILE_IMAGE("profiles/", new String[]{"image/jpeg", "image/png", "image/webp"}),
    THUMBNAIL("thumbnails/", new String[]{"image/jpeg", "image/png", "image/webp"}),
    SHOWREEL_VIDEO("showreels/", new String[]{"video/mp4", "video/webm", "video/quicktime"}),
    PORTFOLIO("portfolios/", new String[]{"application/pdf", "image/jpeg", "image/png"});

    private final String path;
    private final String[] allowedMimeTypes;

    /**
     * 지원하는 MIME 타입인지 확인
     */
    public boolean isAllowedMimeType(String mimeType) {
        if (mimeType == null) return false;
        for (String allowed : allowedMimeTypes) {
            if (allowed.equalsIgnoreCase(mimeType)) {
                return true;
            }
        }
        return false;
    }
}

