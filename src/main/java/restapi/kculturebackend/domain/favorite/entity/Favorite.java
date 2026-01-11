package restapi.kculturebackend.domain.favorite.entity;

import jakarta.persistence.*;
import lombok.*;
import restapi.kculturebackend.common.entity.BaseEntity;
import restapi.kculturebackend.domain.user.entity.User;

import java.util.UUID;

/**
 * 찜 목록 엔티티
 */
@Entity
@Table(name = "favorites", indexes = {
        @Index(name = "idx_favorite_user", columnList = "user_id"),
        @Index(name = "idx_favorite_target", columnList = "target_id, type")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_favorite_user_target", columnNames = {"user_id", "target_id", "type"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Favorite extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "UUID")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "target_id", nullable = false, columnDefinition = "UUID")
    private UUID targetId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private FavoriteType type;

    // 새 찜 생성
    public static Favorite create(User user, UUID targetId, FavoriteType type) {
        return Favorite.builder()
                .user(user)
                .targetId(targetId)
                .type(type)
                .build();
    }
}
