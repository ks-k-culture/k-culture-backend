package restapi.kculturebackend.domain.notification.entity;

import jakarta.persistence.*;
import lombok.*;
import restapi.kculturebackend.common.entity.BaseEntity;
import restapi.kculturebackend.domain.user.entity.User;

import java.util.UUID;

/**
 * 알림 엔티티
 */
@Entity
@Table(name = "notifications", indexes = {
        @Index(name = "idx_notification_user", columnList = "user_id"),
        @Index(name = "idx_notification_read", columnList = "user_id, is_read")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "UUID")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 30)
    private NotificationType type;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "message", length = 500)
    private String message;

    @Column(name = "is_read", nullable = false)
    @Builder.Default
    private Boolean isRead = false;

    @Column(name = "related_id", columnDefinition = "UUID")
    private UUID relatedId;

    // 알림 읽음 처리
    public void markAsRead() {
        this.isRead = true;
    }

    // 알림 생성
    public static Notification create(User user, NotificationType type, String title, String message, UUID relatedId) {
        return Notification.builder()
                .user(user)
                .type(type)
                .title(title)
                .message(message)
                .relatedId(relatedId)
                .build();
    }
}
