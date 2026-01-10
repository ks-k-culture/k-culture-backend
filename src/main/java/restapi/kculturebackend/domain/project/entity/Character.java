package restapi.kculturebackend.domain.project.entity;

import jakarta.persistence.*;
import lombok.*;
import restapi.kculturebackend.common.entity.BaseEntity;
import restapi.kculturebackend.domain.actor.entity.RoleType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 캐릭터(배역) 엔티티
 * 프로젝트 내의 캐릭터/배역 정보
 */
@Entity
@Table(name = "characters")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Character extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "UUID")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(name = "name", nullable = false, length = 100)
    private String name; // 배역명

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 10)
    private Gender gender; // 성별

    @Column(name = "age_range", length = 30)
    private String ageRange; // 나이대 (예: "20대 초반", "30-40")

    @Enumerated(EnumType.STRING)
    @Column(name = "role_type", length = 20)
    private RoleType roleType; // 역할 유형 (주연, 조연 등)

    @Column(name = "description", length = 1000)
    private String description; // 캐릭터 설명

    @ElementCollection
    @CollectionTable(name = "character_keywords", joinColumns = @JoinColumn(name = "character_id"))
    @Column(name = "keyword")
    @Builder.Default
    private List<String> keywords = new ArrayList<>(); // 키워드 (성격, 특징 등)

    @Column(name = "fee", length = 50)
    private String fee; // 출연료

    @Column(name = "is_casting_complete", nullable = false)
    @Builder.Default
    private Boolean isCastingComplete = false; // 캐스팅 완료 여부

    /**
     * 캐릭터 정보 수정
     */
    public void update(String name, Gender gender, String ageRange, RoleType roleType,
                       String description, List<String> keywords, String fee) {
        this.name = name;
        this.gender = gender;
        this.ageRange = ageRange;
        this.roleType = roleType;
        this.description = description;
        this.keywords = keywords != null ? keywords : new ArrayList<>();
        this.fee = fee;
    }

    /**
     * 캐스팅 완료 처리
     */
    public void completeCasting() {
        this.isCastingComplete = true;
    }

    /**
     * 새 캐릭터 생성
     */
    public static Character create(Project project, String name, Gender gender, String ageRange,
                                    RoleType roleType, String description, List<String> keywords, String fee) {
        return Character.builder()
                .project(project)
                .name(name)
                .gender(gender)
                .ageRange(ageRange)
                .roleType(roleType)
                .description(description)
                .keywords(keywords != null ? keywords : new ArrayList<>())
                .fee(fee)
                .build();
    }
}

