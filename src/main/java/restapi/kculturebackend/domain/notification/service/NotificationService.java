package restapi.kculturebackend.domain.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import restapi.kculturebackend.common.exception.ErrorCode;
import restapi.kculturebackend.common.exception.NotFoundException;
import restapi.kculturebackend.domain.notification.dto.NotificationResponse;
import restapi.kculturebackend.domain.notification.entity.Notification;
import restapi.kculturebackend.domain.notification.entity.NotificationType;
import restapi.kculturebackend.domain.notification.repository.NotificationRepository;
import restapi.kculturebackend.domain.user.entity.User;

import java.util.UUID;

/**
 * 알림 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    /**
     * 알림 목록 조회
     */
    @Transactional(readOnly = true)
    public Page<NotificationResponse> getNotifications(User user, Boolean isRead, NotificationType type, Pageable pageable) {
        Page<Notification> notifications;

        if (isRead != null) {
            notifications = notificationRepository.findByUserIdAndIsRead(user.getId(), isRead, pageable);
        } else if (type != null) {
            notifications = notificationRepository.findByUserIdAndType(user.getId(), type, pageable);
        } else {
            notifications = notificationRepository.findByUserId(user.getId(), pageable);
        }

        return notifications.map(NotificationResponse::from);
    }

    /**
     * 읽지 않은 알림 수 조회
     */
    @Transactional(readOnly = true)
    public long getUnreadCount(User user) {
        return notificationRepository.countByUserIdAndIsReadFalse(user.getId());
    }

    /**
     * 알림 읽음 처리
     */
    @Transactional
    public void markAsRead(User user, UUID notificationId) {
        Notification notification = notificationRepository.findByIdAndUserId(notificationId, user.getId())
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOTIFICATION_NOT_FOUND));

        notification.markAsRead();
        notificationRepository.save(notification);

        log.info("Notification marked as read: userId={}, notificationId={}", user.getId(), notificationId);
    }

    /**
     * 모든 알림 읽음 처리
     */
    @Transactional
    public int markAllAsRead(User user) {
        int updatedCount = notificationRepository.markAllAsRead(user.getId());
        log.info("All notifications marked as read: userId={}, count={}", user.getId(), updatedCount);
        return updatedCount;
    }

    /**
     * 알림 생성 (내부 사용)
     */
    @Transactional
    public Notification createNotification(User user, NotificationType type, String title, String message, UUID relatedId) {
        Notification notification = Notification.create(user, type, title, message, relatedId);
        return notificationRepository.save(notification);
    }
}
