package restapi.kculturebackend.domain.job.entity;

import jakarta.persistence.*;
import lombok.*;
import restapi.kculturebackend.common.entity.BaseEntity;
import restapi.kculturebackend.domain.user.entity.User;

import java.util.UUID;

/**
 * 작품구인 엔티티
 */
@Entity
@Table(name = "jobs", indexes = {
        @Index(name = "idx_job_user", columnList = "user_id"),
        @Index(name = "idx_job_status", columnList = "status"),
        @Index(name = "idx_job_category", columnList = "category")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Job extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "UUID")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 30)
    private JobCategory category;

    @Column(name = "is_pumasi", nullable = false)
    @Builder.Default
    private Boolean isPumasi = false;

    @Column(name = "price")
    private Integer price;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description", length = 2000)
    private String description;

    @Column(name = "gender", length = 20)
    private String gender;

    @Column(name = "age_range", length = 30)
    private String ageRange;

    @Column(name = "production", nullable = false, length = 100)
    private String production;

    @Column(name = "work_title", nullable = false, length = 100)
    private String workTitle;

    @Column(name = "shooting_date", length = 50)
    private String shootingDate;

    @Column(name = "shooting_location", length = 200)
    private String shootingLocation;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private JobStatus status = JobStatus.RECRUITING;

    @Column(name = "views", nullable = false)
    @Builder.Default
    private Integer views = 0;

    @Column(name = "contact_email", length = 100)
    private String contactEmail;

    @Column(name = "contact_phone", length = 20)
    private String contactPhone;

    // 조회수 증가
    public void incrementViews() {
        this.views++;
    }

    // 작품구인 수정
    public void update(JobCategory category, Boolean isPumasi, Integer price, String title,
                       String description, String gender, String ageRange, String production,
                       String workTitle, String shootingDate, String shootingLocation,
                       String contactEmail, String contactPhone) {
        this.category = category;
        this.isPumasi = isPumasi;
        this.price = price;
        this.title = title;
        this.description = description;
        this.gender = gender;
        this.ageRange = ageRange;
        this.production = production;
        this.workTitle = workTitle;
        this.shootingDate = shootingDate;
        this.shootingLocation = shootingLocation;
        this.contactEmail = contactEmail;
        this.contactPhone = contactPhone;
    }

    // 마감 처리
    public void close() {
        this.status = JobStatus.CLOSED;
    }
}
