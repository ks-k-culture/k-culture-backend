package restapi.kculturebackend.unit.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import restapi.kculturebackend.domain.actor.entity.ActorProfile;
import restapi.kculturebackend.domain.actor.repository.ActorProfileRepository;
import restapi.kculturebackend.domain.dashboard.dto.ActorDashboardStats;
import restapi.kculturebackend.domain.dashboard.dto.AgencyDashboardStats;
import restapi.kculturebackend.domain.dashboard.dto.RecentActivitiesResponse;
import restapi.kculturebackend.domain.dashboard.entity.Activity;
import restapi.kculturebackend.domain.dashboard.entity.ActivityType;
import restapi.kculturebackend.domain.dashboard.repository.ActivityRepository;
import restapi.kculturebackend.domain.dashboard.repository.ContactRequestRepository;
import restapi.kculturebackend.domain.dashboard.repository.ProfileViewRepository;
import restapi.kculturebackend.domain.dashboard.service.DashboardService;
import restapi.kculturebackend.domain.favorite.entity.FavoriteType;
import restapi.kculturebackend.domain.favorite.repository.FavoriteRepository;
import restapi.kculturebackend.domain.project.repository.CharacterRepository;
import restapi.kculturebackend.domain.project.repository.ProjectRepository;
import restapi.kculturebackend.domain.user.entity.User;
import restapi.kculturebackend.domain.user.entity.UserType;
import restapi.kculturebackend.domain.user.repository.UserRepository;

/**
 * DashboardService 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private ActorProfileRepository actorProfileRepository;
    
    @Mock
    private ProjectRepository projectRepository;
    
    @Mock
    private CharacterRepository characterRepository;
    
    @Mock
    private FavoriteRepository favoriteRepository;
    
    @Mock
    private ProfileViewRepository profileViewRepository;
    
    @Mock
    private ContactRequestRepository contactRequestRepository;
    
    @Mock
    private ActivityRepository activityRepository;
    
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DashboardService dashboardService;

    private User actorUser;
    private User agencyUser;
    private ActorProfile actorProfile;

    @BeforeEach
    void setUp() {
        actorUser = User.builder()
                .id(UUID.randomUUID())
                .email("actor@test.com")
                .password("password")
                .name("테스트배우")
                .type(UserType.ACTOR)
                .isActive(true)
                .build();

        agencyUser = User.builder()
                .id(UUID.randomUUID())
                .email("agency@test.com")
                .password("password")
                .name("테스트에이전시")
                .type(UserType.AGENCY)
                .isActive(true)
                .build();

        actorProfile = ActorProfile.builder()
                .user(actorUser)
                .stageName("테스트배우")
                .birthYear(1995)
                .introduction("안녕하세요")
                .height(175)
                .weight(70)
                .skills(List.of("연기", "춤"))
                .languages(List.of("한국어", "영어"))
                .isProfileComplete(true)
                .build();
    }

    @Nested
    @DisplayName("isActor 테스트")
    class IsActorTest {

        @Test
        @DisplayName("배우 사용자인 경우 true 반환")
        void isActor_WithActorUser_ReturnsTrue() {
            // when
            boolean result = dashboardService.isActor(actorUser);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("에이전시 사용자인 경우 false 반환")
        void isActor_WithAgencyUser_ReturnsFalse() {
            // when
            boolean result = dashboardService.isActor(agencyUser);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("getActorStats 테스트")
    class GetActorStatsTest {

        @Test
        @DisplayName("배우 대시보드 통계 조회 성공")
        void getActorStats_Success() {
            // given
            when(profileViewRepository.countByActorId(actorUser.getId())).thenReturn(100L);
            when(favoriteRepository.countByTargetIdAndType(actorUser.getId(), FavoriteType.ACTOR)).thenReturn(50L);
            when(contactRequestRepository.countByActorId(actorUser.getId())).thenReturn(10L);
            when(actorProfileRepository.findById(actorUser.getId())).thenReturn(Optional.of(actorProfile));
            when(activityRepository.findRecentByUserId(eq(actorUser.getId()), any(PageRequest.class)))
                    .thenReturn(Collections.emptyList());

            // when
            ActorDashboardStats stats = dashboardService.getActorStats(actorUser);

            // then
            assertThat(stats).isNotNull();
            assertThat(stats.getProfileViews()).isEqualTo(100);
            assertThat(stats.getLikes()).isEqualTo(50);
            assertThat(stats.getContactRequests()).isEqualTo(10);
            assertThat(stats.getProfileCompleteness()).isGreaterThan(0);
            assertThat(stats.getRecentActivities()).isEmpty();

            verify(profileViewRepository).countByActorId(actorUser.getId());
            verify(favoriteRepository).countByTargetIdAndType(actorUser.getId(), FavoriteType.ACTOR);
            verify(contactRequestRepository).countByActorId(actorUser.getId());
        }

        @Test
        @DisplayName("프로필 없는 배우의 완성도는 0")
        void getActorStats_WithNoProfile_CompletenessIsZero() {
            // given
            when(profileViewRepository.countByActorId(actorUser.getId())).thenReturn(0L);
            when(favoriteRepository.countByTargetIdAndType(actorUser.getId(), FavoriteType.ACTOR)).thenReturn(0L);
            when(contactRequestRepository.countByActorId(actorUser.getId())).thenReturn(0L);
            when(actorProfileRepository.findById(actorUser.getId())).thenReturn(Optional.empty());
            when(activityRepository.findRecentByUserId(eq(actorUser.getId()), any(PageRequest.class)))
                    .thenReturn(Collections.emptyList());

            // when
            ActorDashboardStats stats = dashboardService.getActorStats(actorUser);

            // then
            assertThat(stats.getProfileCompleteness()).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("getAgencyStats 테스트")
    class GetAgencyStatsTest {

        @Test
        @DisplayName("에이전시 대시보드 통계 조회 성공")
        void getAgencyStats_Success() {
            // given
            when(projectRepository.countActiveProjectsByAgencyUserId(agencyUser.getId())).thenReturn(5L);
            when(favoriteRepository.findByUserIdAndType(eq(agencyUser.getId()), eq(FavoriteType.ACTOR), any(PageRequest.class)))
                    .thenReturn(new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 1000), 20));
            when(contactRequestRepository.countByAgencyId(agencyUser.getId())).thenReturn(15L);
            when(characterRepository.countByAgencyUserId(agencyUser.getId())).thenReturn(30L);

            // when
            AgencyDashboardStats stats = dashboardService.getAgencyStats(agencyUser);

            // then
            assertThat(stats).isNotNull();
            assertThat(stats.getActiveProjects()).isEqualTo(5);
            assertThat(stats.getFavoriteActors()).isEqualTo(20);
            assertThat(stats.getSentContacts()).isEqualTo(15);
            assertThat(stats.getTotalCharacters()).isEqualTo(30);
        }
    }

    @Nested
    @DisplayName("recordProfileView 테스트")
    class RecordProfileViewTest {

        @Test
        @DisplayName("프로필 조회수 기록 성공")
        void recordProfileView_Success() {
            // given
            when(userRepository.findById(actorUser.getId())).thenReturn(Optional.of(actorUser));
            when(profileViewRepository.existsByActorIdAndViewerIdSince(
                    eq(actorUser.getId()), eq(agencyUser.getId()), any())).thenReturn(false);
            when(profileViewRepository.save(any())).thenReturn(null);
            when(activityRepository.save(any())).thenReturn(null);

            // when
            dashboardService.recordProfileView(actorUser.getId(), agencyUser, null);

            // then
            verify(profileViewRepository).save(any());
            verify(activityRepository).save(any());
        }

        @Test
        @DisplayName("본인 프로필 조회는 기록하지 않음")
        void recordProfileView_SelfView_NotRecorded() {
            // given
            when(userRepository.findById(actorUser.getId())).thenReturn(Optional.of(actorUser));

            // when
            dashboardService.recordProfileView(actorUser.getId(), actorUser, null);

            // then
            verify(profileViewRepository, never()).save(any());
            verify(activityRepository, never()).save(any());
        }

        @Test
        @DisplayName("중복 조회는 기록하지 않음")
        void recordProfileView_DuplicateView_NotRecorded() {
            // given
            when(userRepository.findById(actorUser.getId())).thenReturn(Optional.of(actorUser));
            when(profileViewRepository.existsByActorIdAndViewerIdSince(
                    eq(actorUser.getId()), eq(agencyUser.getId()), any())).thenReturn(true);

            // when
            dashboardService.recordProfileView(actorUser.getId(), agencyUser, null);

            // then
            verify(profileViewRepository, never()).save(any());
            verify(activityRepository, never()).save(any());
        }

        @Test
        @DisplayName("비로그인 사용자 IP 기반 조회수 기록")
        void recordProfileView_AnonymousUser_WithIp() {
            // given
            String viewerIp = "192.168.1.100";
            when(userRepository.findById(actorUser.getId())).thenReturn(Optional.of(actorUser));
            when(profileViewRepository.existsByActorIdAndViewerIpSince(
                    eq(actorUser.getId()), eq(viewerIp), any())).thenReturn(false);
            when(profileViewRepository.save(any())).thenReturn(null);
            when(activityRepository.save(any())).thenReturn(null);

            // when
            dashboardService.recordProfileView(actorUser.getId(), null, viewerIp);

            // then
            verify(profileViewRepository).save(any());
            verify(activityRepository).save(any());
        }
    }

    @Nested
    @DisplayName("recordFavoriteActivity 테스트")
    class RecordFavoriteActivityTest {

        @Test
        @DisplayName("찜 활동 기록 성공")
        void recordFavoriteActivity_Success() {
            // given
            when(userRepository.findById(actorUser.getId())).thenReturn(Optional.of(actorUser));
            when(activityRepository.save(any())).thenReturn(null);

            // when
            dashboardService.recordFavoriteActivity(actorUser.getId(), agencyUser);

            // then
            verify(activityRepository).save(any());
        }

        @Test
        @DisplayName("존재하지 않는 배우 찜 활동은 기록하지 않음")
        void recordFavoriteActivity_ActorNotFound_NotRecorded() {
            // given
            UUID nonExistentId = UUID.randomUUID();
            when(userRepository.findById(nonExistentId)).thenReturn(Optional.empty());

            // when
            dashboardService.recordFavoriteActivity(nonExistentId, agencyUser);

            // then
            verify(activityRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("getRecentActivities 테스트")
    class GetRecentActivitiesTest {

        @Test
        @DisplayName("최근 활동 내역 조회 성공")
        void getRecentActivities_Success() {
            // given
            Activity activity = Activity.builder()
                    .id(UUID.randomUUID())
                    .user(actorUser)
                    .type(ActivityType.PROFILE_VIEW)
                    .message("테스트에이전시님이 프로필을 조회했습니다")
                    .relatedUserId(agencyUser.getId())
                    .relatedUserName("테스트에이전시")
                    .build();

            when(activityRepository.findRecentByUserId(eq(actorUser.getId()), any(PageRequest.class)))
                    .thenReturn(List.of(activity));
            when(activityRepository.countByUserId(actorUser.getId())).thenReturn(1L);

            // when
            RecentActivitiesResponse response = dashboardService.getRecentActivities(actorUser, 10);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getActivities()).hasSize(1);
            assertThat(response.getTotal()).isEqualTo(1);
            assertThat(response.getActivities().get(0).getMessage()).contains("테스트에이전시");
        }

        @Test
        @DisplayName("활동 내역이 없는 경우 빈 리스트 반환")
        void getRecentActivities_Empty() {
            // given
            when(activityRepository.findRecentByUserId(eq(actorUser.getId()), any(PageRequest.class)))
                    .thenReturn(Collections.emptyList());
            when(activityRepository.countByUserId(actorUser.getId())).thenReturn(0L);

            // when
            RecentActivitiesResponse response = dashboardService.getRecentActivities(actorUser, 10);

            // then
            assertThat(response.getActivities()).isEmpty();
            assertThat(response.getTotal()).isEqualTo(0);
        }
    }
}
