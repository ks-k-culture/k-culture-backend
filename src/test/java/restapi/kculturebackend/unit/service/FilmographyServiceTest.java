package restapi.kculturebackend.unit.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import restapi.kculturebackend.common.exception.ForbiddenException;
import restapi.kculturebackend.common.exception.NotFoundException;
import restapi.kculturebackend.domain.actor.dto.CreateFilmographyRequest;
import restapi.kculturebackend.domain.actor.dto.FilmographyResponse;
import restapi.kculturebackend.domain.actor.dto.UpdateFilmographyRequest;
import restapi.kculturebackend.domain.actor.entity.ActorProfile;
import restapi.kculturebackend.domain.actor.entity.Filmography;
import restapi.kculturebackend.domain.actor.entity.FilmographyType;
import restapi.kculturebackend.domain.actor.entity.RoleType;
import restapi.kculturebackend.domain.actor.repository.ActorProfileRepository;
import restapi.kculturebackend.domain.actor.repository.FilmographyRepository;
import restapi.kculturebackend.domain.actor.service.FilmographyService;
import restapi.kculturebackend.domain.user.entity.User;
import restapi.kculturebackend.domain.user.entity.UserType;

/**
 * FilmographyService 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
class FilmographyServiceTest {

    @Mock
    private FilmographyRepository filmographyRepository;

    @Mock
    private ActorProfileRepository actorProfileRepository;

    @InjectMocks
    private FilmographyService filmographyService;

    private User actorUser;
    private User agencyUser;
    private ActorProfile actorProfile;
    private Filmography filmography;

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
                .userId(actorUser.getId())
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

        filmography = Filmography.builder()
                .id(UUID.randomUUID())
                .actor(actorProfile)
                .title("테스트 드라마")
                .year(2024)
                .type(FilmographyType.DRAMA)
                .role("주인공 역")
                .roleType(RoleType.LEAD)
                .description("테스트 설명")
                .director("테스트 감독")
                .production("테스트 제작사")
                .build();
    }

    @Nested
    @DisplayName("getFilmographiesByActor 테스트")
    class GetFilmographiesByActorTest {

        @Test
        @DisplayName("배우 필모그래피 목록 조회 성공")
        void getFilmographiesByActor_Success() {
            // given
            when(actorProfileRepository.existsById(actorUser.getId())).thenReturn(true);
            when(filmographyRepository.findByActorId(actorUser.getId())).thenReturn(List.of(filmography));

            // when
            List<FilmographyResponse> result = filmographyService.getFilmographiesByActor(actorUser.getId());

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getTitle()).isEqualTo("테스트 드라마");
            assertThat(result.get(0).getYear()).isEqualTo(2024);
            assertThat(result.get(0).getType()).isEqualTo(FilmographyType.DRAMA);
            verify(filmographyRepository).findByActorId(actorUser.getId());
        }

        @Test
        @DisplayName("존재하지 않는 배우 조회 시 예외 발생")
        void getFilmographiesByActor_NotFound_ThrowsException() {
            // given
            UUID nonExistentId = UUID.randomUUID();
            when(actorProfileRepository.existsById(nonExistentId)).thenReturn(false);

            // when & then
            assertThatThrownBy(() -> filmographyService.getFilmographiesByActor(nonExistentId))
                    .isInstanceOf(NotFoundException.class);
            verify(filmographyRepository, never()).findByActorId(any());
        }
    }

    @Nested
    @DisplayName("getFilmography 테스트")
    class GetFilmographyTest {

        @Test
        @DisplayName("필모그래피 상세 조회 성공")
        void getFilmography_Success() {
            // given
            when(filmographyRepository.findByIdWithActor(filmography.getId())).thenReturn(Optional.of(filmography));

            // when
            FilmographyResponse result = filmographyService.getFilmography(filmography.getId());

            // then
            assertThat(result).isNotNull();
            assertThat(result.getTitle()).isEqualTo("테스트 드라마");
            assertThat(result.getRole()).isEqualTo("주인공 역");
        }

        @Test
        @DisplayName("존재하지 않는 필모그래피 조회 시 예외 발생")
        void getFilmography_NotFound_ThrowsException() {
            // given
            UUID nonExistentId = UUID.randomUUID();
            when(filmographyRepository.findByIdWithActor(nonExistentId)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> filmographyService.getFilmography(nonExistentId))
                    .isInstanceOf(NotFoundException.class);
        }
    }

    @Nested
    @DisplayName("createFilmography 테스트")
    class CreateFilmographyTest {

        @Test
        @DisplayName("필모그래피 생성 성공")
        void createFilmography_Success() {
            // given
            CreateFilmographyRequest request = CreateFilmographyRequest.builder()
                    .title("새 영화")
                    .year(2025)
                    .type(FilmographyType.MOVIE)
                    .role("주연 역")
                    .roleType(RoleType.LEAD)
                    .build();

            when(actorProfileRepository.findById(actorUser.getId())).thenReturn(Optional.of(actorProfile));
            when(filmographyRepository.save(any(Filmography.class))).thenAnswer(invocation -> {
                Filmography saved = invocation.getArgument(0);
                return Filmography.builder()
                        .id(UUID.randomUUID())
                        .actor(saved.getActor())
                        .title(saved.getTitle())
                        .year(saved.getYear())
                        .type(saved.getType())
                        .role(saved.getRole())
                        .roleType(saved.getRoleType())
                        .build();
            });

            // when
            FilmographyResponse result = filmographyService.createFilmography(actorUser, request);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getTitle()).isEqualTo("새 영화");
            assertThat(result.getYear()).isEqualTo(2025);
            verify(filmographyRepository).save(any(Filmography.class));
        }

        @Test
        @DisplayName("에이전시 사용자가 필모그래피 생성 시 예외 발생")
        void createFilmography_AgencyUser_ThrowsException() {
            // given
            CreateFilmographyRequest request = CreateFilmographyRequest.builder()
                    .title("새 영화")
                    .year(2025)
                    .type(FilmographyType.MOVIE)
                    .build();

            // when & then
            assertThatThrownBy(() -> filmographyService.createFilmography(agencyUser, request))
                    .isInstanceOf(ForbiddenException.class);
            verify(filmographyRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("updateFilmography 테스트")
    class UpdateFilmographyTest {

        @Test
        @DisplayName("필모그래피 수정 성공")
        void updateFilmography_Success() {
            // given
            UpdateFilmographyRequest request = UpdateFilmographyRequest.builder()
                    .title("수정된 드라마")
                    .year(2025)
                    .type(FilmographyType.DRAMA)
                    .role("조연 역")
                    .roleType(RoleType.SUPPORTING)
                    .build();

            when(filmographyRepository.findByIdWithActor(filmography.getId())).thenReturn(Optional.of(filmography));
            when(filmographyRepository.save(any(Filmography.class))).thenReturn(filmography);

            // when
            FilmographyResponse result = filmographyService.updateFilmography(actorUser, filmography.getId(), request);

            // then
            assertThat(result).isNotNull();
            verify(filmographyRepository).save(any(Filmography.class));
        }

        @Test
        @DisplayName("다른 배우의 필모그래피 수정 시 예외 발생")
        void updateFilmography_OtherActor_ThrowsException() {
            // given
            User otherActor = User.builder()
                    .id(UUID.randomUUID())
                    .email("other@test.com")
                    .type(UserType.ACTOR)
                    .build();

            UpdateFilmographyRequest request = UpdateFilmographyRequest.builder()
                    .title("수정된 드라마")
                    .year(2025)
                    .type(FilmographyType.DRAMA)
                    .build();

            when(filmographyRepository.findByIdWithActor(filmography.getId())).thenReturn(Optional.of(filmography));

            // when & then
            assertThatThrownBy(() -> filmographyService.updateFilmography(otherActor, filmography.getId(), request))
                    .isInstanceOf(ForbiddenException.class);
            verify(filmographyRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("deleteFilmography 테스트")
    class DeleteFilmographyTest {

        @Test
        @DisplayName("필모그래피 삭제 성공")
        void deleteFilmography_Success() {
            // given
            when(filmographyRepository.findByIdWithActor(filmography.getId())).thenReturn(Optional.of(filmography));

            // when
            filmographyService.deleteFilmography(actorUser, filmography.getId());

            // then
            verify(filmographyRepository).delete(filmography);
        }

        @Test
        @DisplayName("다른 배우의 필모그래피 삭제 시 예외 발생")
        void deleteFilmography_OtherActor_ThrowsException() {
            // given
            User otherActor = User.builder()
                    .id(UUID.randomUUID())
                    .email("other@test.com")
                    .type(UserType.ACTOR)
                    .build();

            when(filmographyRepository.findByIdWithActor(filmography.getId())).thenReturn(Optional.of(filmography));

            // when & then
            assertThatThrownBy(() -> filmographyService.deleteFilmography(otherActor, filmography.getId()))
                    .isInstanceOf(ForbiddenException.class);
            verify(filmographyRepository, never()).delete(any());
        }
    }
}
