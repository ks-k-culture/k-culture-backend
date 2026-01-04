package restapi.kculturebackend.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;
import restapi.kculturebackend.common.entity.BaseEntity;

import java.util.UUID;

/**
 * 사용자 상세 프로필 엔티티
 */
@Entity
@Table(name = "user_profiles")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserProfile extends BaseEntity {

    @Id
    @Column(name = "user_id", columnDefinition = "UUID")
    private UUID userId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "position", length = 50)
    private String position; // 포지션 (배우, 모델, 가수 등)

    @Column(name = "agency", length = 100)
    private String agency; // 소속사

    @Column(name = "phone", length = 20)
    private String phone; // 연락처

    @Column(name = "bio", length = 500)
    private String bio; // 자기소개

    @Column(name = "fee", length = 50)
    private String fee; // 출연료 정보

    @Column(name = "height")
    private Integer height; // 키 (cm)

    @Column(name = "weight")
    private Integer weight; // 몸무게 (kg)

    @OneToOne(mappedBy = "userProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private NotificationSettings notificationSettings;

    // 비즈니스 메서드
    public void updateProfile(String position, String agency, String phone, String bio, 
                              String fee, Integer height, Integer weight) {
        this.position = position;
        this.agency = agency;
        this.phone = phone;
        this.bio = bio;
        this.fee = fee;
        this.height = height;
        this.weight = weight;
    }

    public static UserProfile createDefault(User user) {
        UserProfile profile = UserProfile.builder()
                .user(user)
                .build();
        
        // 기본 알림 설정 생성
        NotificationSettings settings = NotificationSettings.createDefault(profile);
        profile.notificationSettings = settings;
        
        return profile;
    }
}

