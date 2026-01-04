package restapi.kculturebackend.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;
import restapi.kculturebackend.common.entity.BaseEntity;

import java.util.UUID;

/**
 * 알림 설정 엔티티
 */
@Entity
@Table(name = "notification_settings")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class NotificationSettings extends BaseEntity {

    @Id
    @Column(name = "user_id", columnDefinition = "UUID")
    private UUID userId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private UserProfile userProfile;

    @Column(name = "casting_notification", nullable = false)
    @Builder.Default
    private Boolean castingNotification = true;

    @Column(name = "message_notification", nullable = false)
    @Builder.Default
    private Boolean messageNotification = true;

    @Column(name = "marketing_notification", nullable = false)
    @Builder.Default
    private Boolean marketingNotification = false;

    // 비즈니스 메서드
    public void updateSettings(Boolean castingNotification, Boolean messageNotification, 
                               Boolean marketingNotification) {
        this.castingNotification = castingNotification;
        this.messageNotification = messageNotification;
        this.marketingNotification = marketingNotification;
    }

    public static NotificationSettings createDefault(UserProfile userProfile) {
        return NotificationSettings.builder()
                .userProfile(userProfile)
                .castingNotification(true)
                .messageNotification(true)
                .marketingNotification(false)
                .build();
    }
}

