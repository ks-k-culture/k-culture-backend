package restapi.kculturebackend.domain.dashboard.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import restapi.kculturebackend.common.entity.BaseEntity;
import restapi.kculturebackend.domain.user.entity.User;

/**
 * 활동 내역 엔티티
 * 사용자의 프로필 관련 활동(조회, 찜, 섭외 요청 등)을 기록
 */
@Entity
@Table(name = "activities", indexes = {
        @Index(name = "idx_activity_user", columnList = "user_id"),
        @Index(name = "idx_activity_type", columnList = "type"),
        @Index(name = "idx_activity_created", columnList = "created_at DESC")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Activity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "UUID")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 30)
    private ActivityType type;

    @Column(name = "message", nullable = false, length = 500)
    private String message;

    @Column(name = "related_user_id", columnDefinition = "UUID")
    private UUID relatedUserId;

    @Column(name = "related_user_name", length = 100)
    private String relatedUserName;

    @Column(name = "related_entity_id", columnDefinition = "UUID")
    private UUID relatedEntityId;

    public static Activity create(User user, ActivityType type, String message,
                                  UUID relatedUserId, String relatedUserName, UUID relatedEntityId) {
        return Activity.builder()
                .user(user)
                .type(type)
                .message(message)
                .relatedUserId(relatedUserId)
                .relatedUserName(relatedUserName)
                .relatedEntityId(relatedEntityId)
                .build();
    }

    public static Activity profileViewed(User actor, User viewer) {
        String viewerName = viewer != null ? viewer.getName() : "익명 사용자";
        return Activity.builder()
                .user(actor)
                .type(ActivityType.PROFILE_VIEW)
                .message(viewerName + "님이 프로필을 조회했습니다")
                .relatedUserId(viewer != null ? viewer.getId() : null)
                .relatedUserName(viewerName)
                .build();
    }

    public static Activity favorited(User actor, User favoritedBy) {
        return Activity.builder()
                .user(actor)
                .type(ActivityType.FAVORITE)
                .message(favoritedBy.getName() + "님이 프로필을 찜했습니다")
                .relatedUserId(favoritedBy.getId())
                .relatedUserName(favoritedBy.getName())
                .build();
    }

    public static Activity contactReceived(User actor, User agency, UUID projectId) {
        return Activity.builder()
                .user(actor)
                .type(ActivityType.CONTACT_REQUEST)
                .message(agency.getName() + "님으로부터 섭외 요청이 도착했습니다")
                .relatedUserId(agency.getId())
                .relatedUserName(agency.getName())
                .relatedEntityId(projectId)
                .build();
    }
}
