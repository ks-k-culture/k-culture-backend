package restapi.kculturebackend.domain.project.entity;

import jakarta.persistence.*;
import lombok.*;
import restapi.kculturebackend.common.entity.BaseEntity;
import restapi.kculturebackend.domain.agency.entity.AgencyProfile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 프로젝트 엔티티
 * 에이전시가 등록하는 작품/프로젝트
 */
@Entity
@Table(name = "projects")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Project extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "UUID")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agency_id", nullable = false)
    private AgencyProfile agency;

    @Column(name = "project_name", nullable = false, length = 200)
    private String projectName; // 프로젝트명

    @Column(name = "company", length = 100)
    private String company; // 제작사

    @Enumerated(EnumType.STRING)
    @Column(name = "project_type", nullable = false, length = 30)
    private ProjectType projectType; // 프로젝트 유형

    @Column(name = "genre", length = 50)
    private String genre; // 장르

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private ProjectStatus status = ProjectStatus.PLANNING; // 프로젝트 상태

    @Column(name = "description", length = 2000)
    private String description; // 프로젝트 설명

    @Column(name = "director", length = 100)
    private String director; // 감독

    @Column(name = "writer", length = 100)
    private String writer; // 작가

    @Column(name = "thumbnail_url", length = 500)
    private String thumbnailUrl; // 썸네일 이미지

    @Column(name = "start_date")
    private LocalDate startDate; // 시작일

    @Column(name = "end_date")
    private LocalDate endDate; // 종료일

    @Column(name = "filming_location", length = 200)
    private String filmingLocation; // 촬영 장소

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Character> characters = new ArrayList<>();

    /**
     * 프로젝트 정보 수정
     */
    public void update(String projectName, String company, ProjectType projectType, String genre,
                       ProjectStatus status, String description, String director, String writer,
                       String thumbnailUrl, LocalDate startDate, LocalDate endDate, String filmingLocation) {
        this.projectName = projectName;
        this.company = company;
        this.projectType = projectType;
        this.genre = genre;
        this.status = status;
        this.description = description;
        this.director = director;
        this.writer = writer;
        this.thumbnailUrl = thumbnailUrl;
        this.startDate = startDate;
        this.endDate = endDate;
        this.filmingLocation = filmingLocation;
    }

    /**
     * 새 프로젝트 생성
     */
    public static Project create(AgencyProfile agency, String projectName, String company,
                                  ProjectType projectType, String genre, String description,
                                  String director, String writer, String thumbnailUrl,
                                  LocalDate startDate, LocalDate endDate, String filmingLocation) {
        return Project.builder()
                .agency(agency)
                .projectName(projectName)
                .company(company)
                .projectType(projectType)
                .genre(genre)
                .description(description)
                .director(director)
                .writer(writer)
                .thumbnailUrl(thumbnailUrl)
                .startDate(startDate)
                .endDate(endDate)
                .filmingLocation(filmingLocation)
                .build();
    }
}

