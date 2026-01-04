package restapi.kculturebackend.domain.actor.entity;

import jakarta.persistence.*;
import lombok.*;
import restapi.kculturebackend.common.entity.BaseEntity;

import java.util.UUID;

/**
 * 필모그래피 엔티티
 * 배우의 출연작 이력
 */
@Entity
@Table(name = "filmographies")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Filmography extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "UUID")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_id", nullable = false)
    private ActorProfile actor;

    @Column(name = "title", nullable = false, length = 200)
    private String title; // 작품 제목

    @Column(name = "year")
    private Integer year; // 출연 연도

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private FilmographyType type; // 작품 유형 (드라마, 영화, 뮤지컬 등)

    @Column(name = "role", length = 100)
    private String role; // 배역명

    @Enumerated(EnumType.STRING)
    @Column(name = "role_type", length = 20)
    private RoleType roleType; // 역할 유형 (주연, 조연, 단역 등)

    @Column(name = "thumbnail_url", length = 500)
    private String thumbnailUrl; // 썸네일 이미지 URL

    @Column(name = "description", length = 500)
    private String description; // 추가 설명

    @Column(name = "director", length = 100)
    private String director; // 감독

    @Column(name = "production", length = 100)
    private String production; // 제작사/방송사

    /**
     * 필모그래피 정보 수정
     */
    public void update(String title, Integer year, FilmographyType type, String role,
                       RoleType roleType, String thumbnailUrl, String description,
                       String director, String production) {
        this.title = title;
        this.year = year;
        this.type = type;
        this.role = role;
        this.roleType = roleType;
        this.thumbnailUrl = thumbnailUrl;
        this.description = description;
        this.director = director;
        this.production = production;
    }

    /**
     * 새 필모그래피 생성
     */
    public static Filmography create(ActorProfile actor, String title, Integer year,
                                      FilmographyType type, String role, RoleType roleType,
                                      String thumbnailUrl, String description,
                                      String director, String production) {
        return Filmography.builder()
                .actor(actor)
                .title(title)
                .year(year)
                .type(type)
                .role(role)
                .roleType(roleType)
                .thumbnailUrl(thumbnailUrl)
                .description(description)
                .director(director)
                .production(production)
                .build();
    }
}

