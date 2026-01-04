package restapi.kculturebackend.domain.actor.entity;

import jakarta.persistence.*;
import lombok.*;
import restapi.kculturebackend.common.entity.BaseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 쇼릴(영상 포트폴리오) 엔티티
 */
@Entity
@Table(name = "showreels")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Showreel extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "UUID")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_id", nullable = false)
    private ActorProfile actor;

    @Column(name = "title", nullable = false, length = 200)
    private String title; // 쇼릴 제목

    @Column(name = "video_url", nullable = false, length = 500)
    private String videoUrl; // 영상 URL

    @Column(name = "thumbnail_url", length = 500)
    private String thumbnailUrl; // 썸네일 이미지 URL

    @Column(name = "duration")
    private Integer duration; // 영상 길이 (초)

    @Column(name = "work_title", length = 200)
    private String workTitle; // 관련 작품명

    @Column(name = "genre", length = 50)
    private String genre; // 장르

    @Column(name = "description", length = 1000)
    private String description; // 설명

    @ElementCollection
    @CollectionTable(name = "showreel_tags", joinColumns = @JoinColumn(name = "showreel_id"))
    @Column(name = "tag")
    @Builder.Default
    private List<String> tags = new ArrayList<>(); // 태그

    @Column(name = "view_count", nullable = false)
    @Builder.Default
    private Long viewCount = 0L; // 조회수

    /**
     * 쇼릴 정보 수정
     */
    public void update(String title, String videoUrl, String thumbnailUrl, Integer duration,
                       String workTitle, String genre, String description, List<String> tags) {
        this.title = title;
        this.videoUrl = videoUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.duration = duration;
        this.workTitle = workTitle;
        this.genre = genre;
        this.description = description;
        this.tags = tags != null ? tags : new ArrayList<>();
    }

    /**
     * 조회수 증가
     */
    public void incrementViewCount() {
        this.viewCount++;
    }

    /**
     * 새 쇼릴 생성
     */
    public static Showreel create(ActorProfile actor, String title, String videoUrl,
                                   String thumbnailUrl, Integer duration, String workTitle,
                                   String genre, String description, List<String> tags) {
        return Showreel.builder()
                .actor(actor)
                .title(title)
                .videoUrl(videoUrl)
                .thumbnailUrl(thumbnailUrl)
                .duration(duration)
                .workTitle(workTitle)
                .genre(genre)
                .description(description)
                .tags(tags != null ? tags : new ArrayList<>())
                .build();
    }
}

