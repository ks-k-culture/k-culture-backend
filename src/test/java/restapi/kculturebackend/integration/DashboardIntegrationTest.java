package restapi.kculturebackend.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClient;
import org.testcontainers.junit.jupiter.Testcontainers;

import restapi.kculturebackend.config.TestContainersConfig;
import restapi.kculturebackend.domain.actor.entity.ActorProfile;
import restapi.kculturebackend.domain.actor.repository.ActorProfileRepository;
import restapi.kculturebackend.domain.dashboard.entity.Activity;
import restapi.kculturebackend.domain.dashboard.entity.ContactRequest;
import restapi.kculturebackend.domain.dashboard.entity.ProfileView;
import restapi.kculturebackend.domain.dashboard.repository.ActivityRepository;
import restapi.kculturebackend.domain.dashboard.repository.ContactRequestRepository;
import restapi.kculturebackend.domain.dashboard.repository.ProfileViewRepository;
import restapi.kculturebackend.domain.favorite.entity.Favorite;
import restapi.kculturebackend.domain.favorite.entity.FavoriteType;
import restapi.kculturebackend.domain.favorite.repository.FavoriteRepository;
import restapi.kculturebackend.domain.user.entity.User;
import restapi.kculturebackend.domain.user.entity.UserType;
import restapi.kculturebackend.domain.user.repository.UserRepository;
import restapi.kculturebackend.security.jwt.JwtTokenProvider;

/**
 * 대시보드 API 통합 테스트
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("test")
@Import(TestContainersConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DashboardIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ActorProfileRepository actorProfileRepository;

    @Autowired
    private ProfileViewRepository profileViewRepository;

    @Autowired
    private ContactRequestRepository contactRequestRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private RestClient restClient;
    private static User actorUser;
    private static User agencyUser;
    private static String actorToken;
    private static String agencyToken;

    @BeforeEach
    void setUp() {
        restClient = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    @Test
    @Order(1)
    @DisplayName("테스트 데이터 준비")
    void prepareTestData() {
        // 테스트 데이터 정리
        activityRepository.deleteAll();
        profileViewRepository.deleteAll();
        contactRequestRepository.deleteAll();
        favoriteRepository.deleteAll();

        // 배우 사용자 생성 (이미 없는 경우)
        String actorEmail = "dashboard-actor@example.com";
        actorUser = userRepository.findByEmail(actorEmail).orElseGet(() -> {
            User user = User.builder()
                    .email(actorEmail)
                    .password("encodedPassword")
                    .name("대시보드배우")
                    .type(UserType.ACTOR)
                    .isActive(true)
                    .build();
            return userRepository.save(user);
        });

        // 배우 프로필 생성
        if (!actorProfileRepository.existsById(actorUser.getId())) {
            ActorProfile actorProfile = ActorProfile.builder()
                    .user(actorUser)
                    .stageName("테스트배우")
                    .birthYear(1995)
                    .introduction("안녕하세요")
                    .height(175)
                    .weight(70)
                    .skills(List.of("연기", "춤"))
                    .languages(List.of("한국어"))
                    .isProfileComplete(true)
                    .build();
            actorProfileRepository.save(actorProfile);
        }

        // 에이전시 사용자 생성
        String agencyEmail = "dashboard-agency@example.com";
        agencyUser = userRepository.findByEmail(agencyEmail).orElseGet(() -> {
            User user = User.builder()
                    .email(agencyEmail)
                    .password("encodedPassword")
                    .name("대시보드에이전시")
                    .type(UserType.AGENCY)
                    .isActive(true)
                    .build();
            return userRepository.save(user);
        });

        // JWT 토큰 생성
        actorToken = jwtTokenProvider.createAccessToken(actorUser.getEmail(), actorUser.getId().toString());
        agencyToken = jwtTokenProvider.createAccessToken(agencyUser.getEmail(), agencyUser.getId().toString());

        // 테스트 데이터 준비
        // 프로필 조회 기록 추가
        ProfileView profileView = ProfileView.create(actorUser, agencyUser, null);
        profileViewRepository.save(profileView);

        // 찜 추가
        if (!favoriteRepository.existsByUserIdAndTargetIdAndType(agencyUser.getId(), actorUser.getId(), FavoriteType.ACTOR)) {
            Favorite favorite = Favorite.create(agencyUser, actorUser.getId(), FavoriteType.ACTOR);
            favoriteRepository.save(favorite);
        }

        // 섭외 요청 추가
        ContactRequest contactRequest = ContactRequest.create(agencyUser, actorUser, null, null, "섭외 요청합니다");
        contactRequestRepository.save(contactRequest);

        // 활동 내역 추가
        Activity activity = Activity.profileViewed(actorUser, agencyUser);
        activityRepository.save(activity);

        assertThat(actorToken).isNotNull();
        assertThat(agencyToken).isNotNull();
    }

    @Test
    @Order(2)
    @DisplayName("배우 대시보드 통계 조회 - 인증 성공")
    void getActorDashboardStats_Success() {
        // when
        ResponseEntity<Map> responseEntity = restClient.get()
                .uri("/api/dashboard/stats")
                .header("Authorization", "Bearer " + actorToken)
                .retrieve()
                .toEntity(Map.class);

        // then
        assertThat(responseEntity.getStatusCode().is2xxSuccessful()).isTrue();
        
        Map response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.get("success")).isEqualTo(true);
        
        Map<String, Object> data = (Map<String, Object>) response.get("data");
        assertThat(data.get("profileViews")).isNotNull();
        assertThat(data.get("likes")).isNotNull();
        assertThat(data.get("contactRequests")).isNotNull();
        assertThat(data.get("profileCompleteness")).isNotNull();
        assertThat(data.get("recentActivities")).isNotNull();
    }

    @Test
    @Order(3)
    @DisplayName("에이전시 대시보드 통계 조회 - 인증 성공")
    void getAgencyDashboardStats_Success() {
        // when
        ResponseEntity<Map> responseEntity = restClient.get()
                .uri("/api/dashboard/stats")
                .header("Authorization", "Bearer " + agencyToken)
                .retrieve()
                .toEntity(Map.class);

        // then
        assertThat(responseEntity.getStatusCode().is2xxSuccessful()).isTrue();
        
        Map response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.get("success")).isEqualTo(true);
        
        Map<String, Object> data = (Map<String, Object>) response.get("data");
        assertThat(data.get("activeProjects")).isNotNull();
        assertThat(data.get("favoriteActors")).isNotNull();
        assertThat(data.get("sentContacts")).isNotNull();
        assertThat(data.get("totalCharacters")).isNotNull();
    }

    @Test
    @Order(4)
    @DisplayName("최근 활동 내역 조회 - 성공")
    void getRecentActivities_Success() {
        // when
        ResponseEntity<Map> responseEntity = restClient.get()
                .uri("/api/dashboard/activities?limit=10")
                .header("Authorization", "Bearer " + actorToken)
                .retrieve()
                .toEntity(Map.class);

        // then
        assertThat(responseEntity.getStatusCode().is2xxSuccessful()).isTrue();
        
        Map response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.get("success")).isEqualTo(true);
        
        Map<String, Object> data = (Map<String, Object>) response.get("data");
        assertThat(data.get("activities")).isNotNull();
        assertThat(data.get("total")).isNotNull();
    }
}
