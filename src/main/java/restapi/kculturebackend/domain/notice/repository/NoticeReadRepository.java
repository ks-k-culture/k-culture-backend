package restapi.kculturebackend.domain.notice.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import restapi.kculturebackend.domain.notice.entity.Notice;
import restapi.kculturebackend.domain.notice.entity.NoticeRead;
import restapi.kculturebackend.domain.user.entity.User;

/**
 * 공지사항 읽음 기록 리포지토리
 */
@Repository
public interface NoticeReadRepository extends JpaRepository<NoticeRead, UUID> {

    Optional<NoticeRead> findByUserAndNotice(User user, Notice notice);

    boolean existsByUserAndNotice(User user, Notice notice);

    @Query("SELECT nr.notice.id FROM NoticeRead nr WHERE nr.user.id = :userId")
    List<UUID> findReadNoticeIdsByUserId(@Param("userId") UUID userId);

    long countByUserId(UUID userId);
}
