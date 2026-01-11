package restapi.kculturebackend.infrastructure.storage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 영상 업로드 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UploadVideoResponse {
    private String url;
    private String thumbnailUrl;
    private String filename;
    private Long size;
    private String duration;
    private String mimeType;
}
