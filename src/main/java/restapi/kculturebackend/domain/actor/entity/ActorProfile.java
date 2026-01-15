package restapi.kculturebackend.domain.actor.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import restapi.kculturebackend.common.entity.BaseEntity;
import restapi.kculturebackend.domain.user.entity.User;

/**
 * 배우 프로필 엔티티
 * User(ACTOR 타입)와 1:1 관계
 */
@Entity
@Table(name = "actor_profiles", indexes = {
        @Index(name = "idx_actor_gender", columnList = "gender"),
        @Index(name = "idx_actor_birth_year", columnList = "birth_year"),
        @Index(name = "idx_actor_height", columnList = "height"),
        @Index(name = "idx_actor_profile_complete", columnList = "is_profile_complete"),
        @Index(name = "idx_actor_category", columnList = "category")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ActorProfile extends BaseEntity {

    @Id
    @Column(name = "user_id", columnDefinition = "UUID")
    private UUID userId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "stage_name", length = 50)
    private String stageName; // 활동명

    @Column(name = "birth_year")
    private Integer birthYear; // 출생년도

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 10)
    private Gender gender; // 성별

    @Enumerated(EnumType.STRING)
    @Column(name = "category", length = 20)
    @Builder.Default
    private ActorCategory category = ActorCategory.ACTOR; // 구분 (배우/모델)

    @Column(name = "introduction", length = 500)
    private String introduction; // 한 줄 소개

    @Column(name = "nationality", length = 50)
    private String nationality; // 국적

    @Column(name = "height")
    private Integer height; // 키 (cm)

    @Column(name = "weight")
    private Integer weight; // 몸무게 (kg)

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "actor_skills", joinColumns = @JoinColumn(name = "actor_id"))
    @Column(name = "skill")
    @Builder.Default
    private List<String> skills = new ArrayList<>(); // 스킬 목록

    @ElementCollection
    @CollectionTable(name = "actor_languages", joinColumns = @JoinColumn(name = "actor_id"))
    @Column(name = "language")
    @Builder.Default
    private List<String> languages = new ArrayList<>(); // 가능 언어

    @Column(name = "agency", length = 100)
    private String agency; // 소속사

    @Column(name = "is_profile_complete", nullable = false)
    @Builder.Default
    private Boolean isProfileComplete = false; // 프로필 완성 여부

    @Column(name = "view_count", nullable = false)
    @Builder.Default
    private Long viewCount = 0L; // 조회수

    // 비즈니스 메서드
    public void updateProfile(String stageName, Integer birthYear, String introduction,
                              String nationality, Integer height, Integer weight,
                              List<String> skills, List<String> languages, String agency) {
        this.stageName = stageName;
        this.birthYear = birthYear;
        this.introduction = introduction;
        this.nationality = nationality;
        this.height = height;
        this.weight = weight;
        this.skills = skills != null ? skills : new ArrayList<>();
        this.languages = languages != null ? languages : new ArrayList<>();
        this.agency = agency;
        this.isProfileComplete = true;
    }

    public void updateGenderAndCategory(Gender gender, ActorCategory category) {
        this.gender = gender;
        this.category = category;
    }

    public void incrementViewCount() {
        this.viewCount++;
    }

    public static ActorProfile createDefault(User user) {
        return ActorProfile.builder()
                .user(user)
                .build();
    }
}

