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
 * 섭외 요청 엔티티
 * 에이전시가 배우에게 보내는 섭외 요청
 */
@Entity
@Table(name = "contact_requests", indexes = {
        @Index(name = "idx_contact_request_actor", columnList = "actor_id"),
        @Index(name = "idx_contact_request_agency", columnList = "agency_id"),
        @Index(name = "idx_contact_request_status", columnList = "status"),
        @Index(name = "idx_contact_request_created", columnList = "created_at")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ContactRequest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "UUID")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agency_id", nullable = false)
    private User agency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_id", nullable = false)
    private User actor;

    @Column(name = "project_id", columnDefinition = "UUID")
    private UUID projectId;

    @Column(name = "character_id", columnDefinition = "UUID")
    private UUID characterId;

    @Column(name = "message", length = 1000)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private ContactRequestStatus status = ContactRequestStatus.PENDING;

    public static ContactRequest create(User agency, User actor, UUID projectId, UUID characterId, String message) {
        return ContactRequest.builder()
                .agency(agency)
                .actor(actor)
                .projectId(projectId)
                .characterId(characterId)
                .message(message)
                .build();
    }

    public void updateStatus(ContactRequestStatus newStatus) {
        this.status = newStatus;
    }
}
