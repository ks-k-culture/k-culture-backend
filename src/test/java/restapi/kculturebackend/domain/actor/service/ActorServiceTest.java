package restapi.kculturebackend.domain.actor.service;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import restapi.kculturebackend.domain.actor.dto.ActorSearchRequest;
import restapi.kculturebackend.domain.actor.dto.ActorSummaryResponse;
import restapi.kculturebackend.domain.actor.entity.ActorCategory;
import restapi.kculturebackend.domain.actor.entity.ActorProfile;
import restapi.kculturebackend.domain.actor.entity.Gender;
import restapi.kculturebackend.domain.actor.repository.ActorProfileRepository;
import restapi.kculturebackend.domain.dashboard.repository.ActivityRepository;
import restapi.kculturebackend.domain.dashboard.repository.ContactRequestRepository;
import restapi.kculturebackend.domain.dashboard.repository.ProfileViewRepository;
import restapi.kculturebackend.domain.dashboard.service.DashboardService;
import restapi.kculturebackend.domain.favorite.repository.FavoriteRepository;
import restapi.kculturebackend.domain.user.entity.User;
import restapi.kculturebackend.domain.user.entity.UserType;
import restapi.kculturebackend.domain.user.repository.UserRepository;

/**
 * ActorService 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
class ActorServiceTest {

    @Mock
    private ActorProfileRepository actorProfileRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DashboardService dashboardService;

    @Mock
    private ContactRequestRepository contactRequestRepository;

    @Mock
    private ActivityRepository activityRepository;

    @Mock
    private ProfileViewRepository profileViewRepository;

    @Mock
    private FavoriteRepository favoriteRepository;

    @InjectMocks
    private ActorService actorService;

    private User mockUser;
    private ActorProfile mockActorProfile;

    @BeforeEach
    void setUp() {
        mockUser = User.builder()
                .id(UUID.randomUUID())
                .email("test@test.com")
                .password("password")
                .name("테스트 배우")
                .type(UserType.ACTOR)
                .profileImage("https://example.com/image.jpg")
                .isActive(true)
                .build();

        mockActorProfile = ActorProfile.builder()
                .user(mockUser)
                .stageName("테스트 배우")
                .birthYear(1995)
                .gender(Gender.MALE)
                .category(ActorCategory.ACTOR)
                .introduction("테스트 소개")
                .nationality("한국")
                .height(180)
                .weight(70)
                .agency("테스트 소속사")
                .isProfileComplete(true)
                .viewCount(100L)
                .skills(List.of("연기", "춤"))
                .languages(List.of("한국어", "영어"))
                .build();
    }

    @Nested
    @DisplayName("배우 목록 조회")
    class GetActors {

        @Test
        @DisplayName("프로필 완성된 배우 목록을 페이징하여 조회한다")
        void getActors_success() {
            // given
            Pageable pageable = PageRequest.of(0, 20);
            Page<ActorProfile> actorPage = new PageImpl<>(List.of(mockActorProfile), pageable, 1);
            
            when(actorProfileRepository.findByIsProfileCompleteTrue(any(Pageable.class)))
                    .thenReturn(actorPage);

            // when
            Page<ActorSummaryResponse> result = actorService.getActors(pageable);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getStageName()).isEqualTo("테스트 배우");
        }

        @Test
        @DisplayName("배우가 없을 때 빈 페이지를 반환한다")
        void getActors_empty() {
            // given
            Pageable pageable = PageRequest.of(0, 20);
            Page<ActorProfile> emptyPage = new PageImpl<>(List.of(), pageable, 0);
            
            when(actorProfileRepository.findByIsProfileCompleteTrue(any(Pageable.class)))
                    .thenReturn(emptyPage);

            // when
            Page<ActorSummaryResponse> result = actorService.getActors(pageable);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).isEmpty();
            assertThat(result.getTotalElements()).isZero();
        }
    }

    @Nested
    @DisplayName("배우 고급 검색")
    class SearchActorsAdvanced {

        @Test
        @DisplayName("필터 조건으로 배우를 검색한다")
        void searchActorsAdvanced_withFilters() {
            // given
            Pageable pageable = PageRequest.of(0, 20);
            ActorSearchRequest request = ActorSearchRequest.builder()
                    .gender("남자")
                    .category("배우")
                    .ageMin(20)
                    .ageMax(30)
                    .sortBy("recent")
                    .build();
            
            Page<ActorProfile> actorPage = new PageImpl<>(List.of(mockActorProfile), pageable, 1);
            
            when(actorProfileRepository.searchWithFilters(any(ActorSearchRequest.class), any(Pageable.class)))
                    .thenReturn(actorPage);

            // when
            Page<ActorSummaryResponse> result = actorService.searchActorsAdvanced(request, pageable);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
        }

        @Test
        @DisplayName("키워드로 배우를 검색한다")
        void searchActorsAdvanced_withKeyword() {
            // given
            Pageable pageable = PageRequest.of(0, 20);
            ActorSearchRequest request = ActorSearchRequest.builder()
                    .keyword("테스트")
                    .build();
            
            Page<ActorProfile> actorPage = new PageImpl<>(List.of(mockActorProfile), pageable, 1);
            
            when(actorProfileRepository.searchWithFilters(any(ActorSearchRequest.class), any(Pageable.class)))
                    .thenReturn(actorPage);

            // when
            Page<ActorSummaryResponse> result = actorService.searchActorsAdvanced(request, pageable);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getStageName()).contains("테스트");
        }

        @Test
        @DisplayName("정렬 기준으로 배우를 정렬한다")
        void searchActorsAdvanced_withSort() {
            // given
            Pageable pageable = PageRequest.of(0, 20);
            ActorSearchRequest request = ActorSearchRequest.builder()
                    .sortBy("views_high")
                    .build();
            
            Page<ActorProfile> actorPage = new PageImpl<>(List.of(mockActorProfile), pageable, 1);
            
            when(actorProfileRepository.searchWithFilters(any(ActorSearchRequest.class), any(Pageable.class)))
                    .thenReturn(actorPage);

            // when
            Page<ActorSummaryResponse> result = actorService.searchActorsAdvanced(request, pageable);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("배우 이름 검색")
    class SearchActors {

        @Test
        @DisplayName("이름으로 배우를 검색한다")
        void searchActors_byName() {
            // given
            Pageable pageable = PageRequest.of(0, 20);
            Page<ActorProfile> actorPage = new PageImpl<>(List.of(mockActorProfile), pageable, 1);
            
            when(actorProfileRepository.searchByName(any(String.class), any(Pageable.class)))
                    .thenReturn(actorPage);

            // when
            Page<ActorSummaryResponse> result = actorService.searchActors("테스트", pageable);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
        }
    }
}
