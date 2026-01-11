package restapi.kculturebackend.domain.notification.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import restapi.kculturebackend.domain.notification.entity.Notification;
import restapi.kculturebackend.domain.notification.entity.NotificationType;

import java.util.Optional;
import java.util.UUID;

/**
 * 알림 레포지토리
 */
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    /**
     * 사용자의 알림 목록 조회 (페이징)
     */
    Page<Notification> findByUserId(UUID userId, Pageable pageable);

    /**
     * 사용자의 알림 목록 조회 (읽음 상태별)
     */
    Page<Notification> findByUserIdAndIsRead(UUID userId, Boolean isRead, Pageable pageable);

    /**
     * 사용자의 알림 목록 조회 (타입별)
     */
    Page<Notification> findByUserIdAndType(UUID userId, NotificationType type, Pageable pageable);

    /**
     * 사용자의 읽지 않은 알림 수
     */
    long countByUserIdAndIsReadFalse(UUID userId);

    /**
     * 사용자의 특정 알림 조회
     */
    @Query("SELECT n FROM Notification n WHERE n.id = :notificationId AND n.user.id = :userId")
    Optional<Notification> findByIdAndUserId(@Param("notificationId") UUID notificationId, @Param("userId") UUID userId);

    /**
     * 사용자의 모든 알림 읽음 처리
     */
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.user.id = :userId AND n.isRead = false")
    int markAllAsRead(@Param("userId") UUID userId);
}
