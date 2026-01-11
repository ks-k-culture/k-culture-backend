package restapi.kculturebackend.infrastructure.storage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 이미지 업로드 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UploadImageResponse {
    private String url;
    private String filename;
    private Long size;
    private String mimeType;
}
