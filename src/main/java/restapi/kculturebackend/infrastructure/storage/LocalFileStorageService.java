package restapi.kculturebackend.infrastructure.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import restapi.kculturebackend.common.exception.BusinessException;
import restapi.kculturebackend.common.exception.ErrorCode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * 로컬 파일시스템 저장소 서비스 (개발용)
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "file.storage.type", havingValue = "local", matchIfMissing = true)
public class LocalFileStorageService implements FileStorageService {

    private final Path uploadPath;
    private final String baseUrl;

    public LocalFileStorageService(
            @Value("${file.storage.local.upload-dir:uploads}") String uploadDir,
            @Value("${file.storage.local.base-url:http://localhost:8080/uploads}") String baseUrl) {
        this.uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        this.baseUrl = baseUrl;
        
        try {
            Files.createDirectories(this.uploadPath);
            log.info("Local file storage initialized at: {}", this.uploadPath);
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory", e);
        }
    }

    @Override
    public UploadResult upload(MultipartFile file, FileType type) {
        // 파일 유효성 검사
        validateFile(file, type);

        // 고유 파일명 생성
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = getExtension(originalFilename);
        String newFilename = UUID.randomUUID().toString() + extension;

        // 저장 경로 생성
        Path typePath = uploadPath.resolve(type.getPath());
        try {
            Files.createDirectories(typePath);
        } catch (IOException e) {
            log.error("Failed to create directory: {}", typePath, e);
            throw new BusinessException(ErrorCode.FILE_UPLOAD_FAILED);
        }

        Path targetPath = typePath.resolve(newFilename);

        try {
            // 파일 저장
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            log.info("File uploaded: {} -> {}", originalFilename, targetPath);

            String fileUrl = baseUrl + "/" + type.getPath() + newFilename;
            String fileKey = type.getPath() + newFilename;

            return UploadResult.builder()
                    .url(fileUrl)
                    .key(fileKey)
                    .filename(originalFilename)
                    .size(file.getSize())
                    .mimeType(file.getContentType())
                    .build();

        } catch (IOException e) {
            log.error("Failed to upload file: {}", originalFilename, e);
            throw new BusinessException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    @Override
    public void delete(String fileUrl) {
        if (fileUrl == null || !fileUrl.startsWith(baseUrl)) {
            log.warn("Invalid file URL for deletion: {}", fileUrl);
            return;
        }

        String relativePath = fileUrl.substring(baseUrl.length() + 1);
        Path filePath = uploadPath.resolve(relativePath);

        try {
            if (Files.deleteIfExists(filePath)) {
                log.info("File deleted: {}", filePath);
            } else {
                log.warn("File not found for deletion: {}", filePath);
            }
        } catch (IOException e) {
            log.error("Failed to delete file: {}", filePath, e);
        }
    }

    @Override
    public boolean exists(String fileUrl) {
        if (fileUrl == null || !fileUrl.startsWith(baseUrl)) {
            return false;
        }

        String relativePath = fileUrl.substring(baseUrl.length() + 1);
        Path filePath = uploadPath.resolve(relativePath);
        return Files.exists(filePath);
    }

    /**
     * 파일 유효성 검사
     */
    private void validateFile(MultipartFile file, FileType type) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.FILE_UPLOAD_FAILED, "파일이 비어있습니다.");
        }

        String mimeType = file.getContentType();
        if (!type.isAllowedMimeType(mimeType)) {
            throw new BusinessException(ErrorCode.INVALID_FILE_TYPE,
                    "지원하지 않는 파일 형식입니다: " + mimeType);
        }
    }

    /**
     * 파일 확장자 추출
     */
    private String getExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        return dotIndex > 0 ? filename.substring(dotIndex) : "";
    }
}

