package restapi.kculturebackend.infrastructure.storage;

import org.springframework.web.multipart.MultipartFile;

/**
 * 파일 저장소 서비스 인터페이스
 * 
 * 구현체:
 * - LocalFileStorageService: 로컬 파일시스템 저장 (개발용)
 * - OciFileStorageService: OCI Object Storage 저장 (프로덕션용)
 */
public interface FileStorageService {

    /**
     * 파일 업로드
     * 
     * @param file 업로드할 파일
     * @param type 파일 타입 (저장 경로 결정)
     * @return 업로드 결과
     */
    UploadResult upload(MultipartFile file, FileType type);

    /**
     * 파일 삭제
     * 
     * @param fileUrl 삭제할 파일 URL
     */
    void delete(String fileUrl);

    /**
     * 파일 존재 여부 확인
     * 
     * @param fileUrl 확인할 파일 URL
     * @return 존재 여부
     */
    boolean exists(String fileUrl);
}

