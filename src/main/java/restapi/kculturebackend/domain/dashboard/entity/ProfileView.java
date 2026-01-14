package restapi.kculturebackend.domain.dashboard.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
 * 프로필 조회 기록 엔티티
 * 배우 프로필 조회 이력을 저장하여 조회수 통계를 집계
 */
@Entity
@Table(name = "profile_views", indexes = {
        @Index(name = "idx_profile_view_actor", columnList = "actor_id"),
        @Index(name = "idx_profile_view_viewer", columnList = "viewer_id"),
        @Index(name = "idx_profile_view_created", columnList = "created_at")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ProfileView extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "UUID")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_id", nullable = false)
    private User actor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "viewer_id")
    private User viewer;

    @Column(name = "viewer_ip", length = 45)
    private String viewerIp;

    public static ProfileView create(User actor, User viewer, String viewerIp) {
        return ProfileView.builder()
                .actor(actor)
                .viewer(viewer)
                .viewerIp(viewerIp)
                .build();
    }
}
