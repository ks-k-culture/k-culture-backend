package restapi.kculturebackend.domain.agency.entity;

import jakarta.persistence.*;
import lombok.*;
import restapi.kculturebackend.common.entity.BaseEntity;
import restapi.kculturebackend.domain.user.entity.User;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 에이전시 프로필 엔티티
 * User(AGENCY 타입)와 1:1 관계
 */
@Entity
@Table(name = "agency_profiles")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class AgencyProfile extends BaseEntity {

    @Id
    @Column(name = "user_id", columnDefinition = "UUID")
    private UUID userId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "agency_name", length = 100)
    private String agencyName; // 에이전시명

    @Column(name = "representative_name", length = 50)
    private String representativeName; // 대표자명

    @Column(name = "founded_year", length = 4)
    private String foundedYear; // 설립년도

    @Column(name = "business_number", length = 20)
    private String businessNumber; // 사업자등록번호

    @Column(name = "address", length = 200)
    private String address; // 주소

    @Column(name = "phone", length = 20)
    private String phone; // 연락처

    @Column(name = "website", length = 200)
    private String website; // 웹사이트

    @Column(name = "introduction", length = 1000)
    private String introduction; // 회사 소개

    @ElementCollection
    @CollectionTable(name = "agency_specialties", joinColumns = @JoinColumn(name = "agency_id"))
    @Column(name = "specialty")
    @Builder.Default
    private List<String> specialties = new ArrayList<>(); // 전문 분야

    @Column(name = "is_profile_complete", nullable = false)
    @Builder.Default
    private Boolean isProfileComplete = false; // 프로필 완성 여부

    // 비즈니스 메서드
    public void updateProfile(String agencyName, String representativeName, String foundedYear,
                              String businessNumber, String address, String phone,
                              String website, String introduction, List<String> specialties) {
        this.agencyName = agencyName;
        this.representativeName = representativeName;
        this.foundedYear = foundedYear;
        this.businessNumber = businessNumber;
        this.address = address;
        this.phone = phone;
        this.website = website;
        this.introduction = introduction;
        this.specialties = specialties != null ? specialties : new ArrayList<>();
        this.isProfileComplete = true;
    }

    public static AgencyProfile createDefault(User user) {
        return AgencyProfile.builder()
                .user(user)
                .build();
    }
}

