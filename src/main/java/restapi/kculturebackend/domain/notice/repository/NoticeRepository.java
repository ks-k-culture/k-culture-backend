package restapi.kculturebackend.domain.notice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import restapi.kculturebackend.domain.notice.entity.Notice;
import restapi.kculturebackend.domain.notice.entity.NoticeType;

import java.util.UUID;

/**
 * 공지사항 레포지토리
 */
public interface NoticeRepository extends JpaRepository<Notice, UUID> {

    /**
     * 공지사항 목록 조회 (타입별)
     */
    Page<Notice> findByType(NoticeType type, Pageable pageable);

    /**
     * 공지사항 목록 조회 (전체)
     */
    Page<Notice> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
