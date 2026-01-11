package restapi.kculturebackend.infrastructure.storage;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import restapi.kculturebackend.common.dto.ApiResponse;
import restapi.kculturebackend.common.exception.BusinessException;
import restapi.kculturebackend.common.exception.ErrorCode;
import restapi.kculturebackend.domain.user.entity.User;
import restapi.kculturebackend.infrastructure.storage.dto.UploadImageResponse;
import restapi.kculturebackend.infrastructure.storage.dto.UploadVideoResponse;

/**
 * 파일 업로드 API 컨트롤러
 */
@Tag(name = "Upload", description = "파일 업로드 관련 API (이미지, 영상)")
@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class UploadController {

    private final FileStorageService fileStorageService;

    // 이미지 업로드
    @Operation(summary = "이미지 업로드", description = "프로필, 썸네일, 포트폴리오 이미지를 업로드합니다.")
    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<UploadImageResponse>> uploadImage(
            @AuthenticationPrincipal User user,
            @Parameter(description = "이미지 파일 (jpg, png, webp)") 
            @RequestPart("file") MultipartFile file,
            @Parameter(description = "업로드 타입 (profile, thumbnail, portfolio)") 
            @RequestParam("type") String type) {

        FileType fileType = mapImageType(type);
        UploadResult result = fileStorageService.upload(file, fileType);

        UploadImageResponse response = UploadImageResponse.builder()
                .url(result.getUrl())
                .filename(result.getFilename())
                .size(result.getSize())
                .mimeType(result.getMimeType())
                .build();

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 영상 업로드
    @Operation(summary = "영상 업로드", description = "쇼릴 영상을 업로드합니다.")
    @PostMapping(value = "/video", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<UploadVideoResponse>> uploadVideo(
            @AuthenticationPrincipal User user,
            @Parameter(description = "영상 파일 (mp4, mov)") 
            @RequestPart("file") MultipartFile file,
            @Parameter(description = "업로드 타입 (showreel)") 
            @RequestParam("type") String type) {

        if (!"showreel".equalsIgnoreCase(type)) {
            throw new BusinessException(ErrorCode.INVALID_FILE_TYPE, "지원하지 않는 업로드 타입입니다.");
        }

        UploadResult result = fileStorageService.upload(file, FileType.SHOWREEL_VIDEO);

        // 썸네일 URL은 현재 구현에서는 null (추후 영상 처리 서비스 추가 시 생성)
        UploadVideoResponse response = UploadVideoResponse.builder()
                .url(result.getUrl())
                .thumbnailUrl(null)
                .filename(result.getFilename())
                .size(result.getSize())
                .duration(null) // 영상 길이는 추후 메타데이터 추출 시 구현
                .mimeType(result.getMimeType())
                .build();

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 이미지 타입 문자열을 FileType으로 매핑
    private FileType mapImageType(String type) {
        return switch (type.toLowerCase()) {
            case "profile" -> FileType.PROFILE_IMAGE;
            case "thumbnail" -> FileType.THUMBNAIL;
            case "portfolio" -> FileType.PORTFOLIO;
            default -> throw new BusinessException(ErrorCode.INVALID_FILE_TYPE, 
                    "지원하지 않는 업로드 타입입니다: " + type);
        };
    }
}
