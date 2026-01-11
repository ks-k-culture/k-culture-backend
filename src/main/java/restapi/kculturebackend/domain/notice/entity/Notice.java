package restapi.kculturebackend.domain.notice.entity;

import jakarta.persistence.*;
import lombok.*;
import restapi.kculturebackend.common.entity.BaseEntity;

import java.util.UUID;

/**
 * 공지사항 엔티티
 */
@Entity
@Table(name = "notices", indexes = {
        @Index(name = "idx_notice_type", columnList = "type"),
        @Index(name = "idx_notice_created", columnList = "created_at DESC")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Notice extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "UUID")
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private NoticeType type;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "views", nullable = false)
    @Builder.Default
    private Integer views = 0;

    /**
     * 조회수 증가
     */
    public void incrementViews() {
        this.views++;
    }
}
