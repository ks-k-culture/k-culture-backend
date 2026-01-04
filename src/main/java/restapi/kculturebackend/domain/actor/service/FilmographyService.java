package restapi.kculturebackend.domain.actor.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import restapi.kculturebackend.common.exception.ErrorCode;
import restapi.kculturebackend.common.exception.ForbiddenException;
import restapi.kculturebackend.common.exception.NotFoundException;
import restapi.kculturebackend.domain.actor.dto.CreateFilmographyRequest;
import restapi.kculturebackend.domain.actor.dto.FilmographyResponse;
import restapi.kculturebackend.domain.actor.dto.UpdateFilmographyRequest;
import restapi.kculturebackend.domain.actor.entity.ActorProfile;
import restapi.kculturebackend.domain.actor.entity.Filmography;
import restapi.kculturebackend.domain.actor.repository.ActorProfileRepository;
import restapi.kculturebackend.domain.actor.repository.FilmographyRepository;
import restapi.kculturebackend.domain.user.entity.User;
import restapi.kculturebackend.domain.user.entity.UserType;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 필모그래피 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FilmographyService {

    private final FilmographyRepository filmographyRepository;
    private final ActorProfileRepository actorProfileRepository;

    /**
     * 특정 배우의 필모그래피 목록 조회
     */
    @Transactional(readOnly = true)
    public List<FilmographyResponse> getFilmographiesByActor(UUID actorId) {
        // 배우 존재 확인
        if (!actorProfileRepository.existsById(actorId)) {
            throw new NotFoundException(ErrorCode.ACTOR_PROFILE_NOT_FOUND);
        }

        return filmographyRepository.findByActorId(actorId).stream()
                .map(FilmographyResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 필모그래피 상세 조회
     */
    @Transactional(readOnly = true)
    public FilmographyResponse getFilmography(UUID filmographyId) {
        Filmography filmography = filmographyRepository.findByIdWithActor(filmographyId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.FILMOGRAPHY_NOT_FOUND));

        return FilmographyResponse.from(filmography);
    }

    /**
     * 내 필모그래피 목록 조회
     */
    @Transactional(readOnly = true)
    public List<FilmographyResponse> getMyFilmographies(User user) {
        validateActorUser(user);

        return filmographyRepository.findByActorId(user.getId()).stream()
                .map(FilmographyResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 필모그래피 생성
     */
    @Transactional
    public FilmographyResponse createFilmography(User user, CreateFilmographyRequest request) {
        validateActorUser(user);

        ActorProfile actor = actorProfileRepository.findById(user.getId())
                .orElseThrow(() -> new NotFoundException(ErrorCode.ACTOR_PROFILE_NOT_FOUND));

        Filmography filmography = Filmography.create(
                actor,
                request.getTitle(),
                request.getYear(),
                request.getType(),
                request.getRole(),
                request.getRoleType(),
                request.getThumbnailUrl(),
                request.getDescription(),
                request.getDirector(),
                request.getProduction()
        );

        Filmography saved = filmographyRepository.save(filmography);
        log.info("Filmography created: {} for actor: {}", saved.getId(), user.getId());

        return FilmographyResponse.from(saved);
    }

    /**
     * 필모그래피 수정
     */
    @Transactional
    public FilmographyResponse updateFilmography(User user, UUID filmographyId, UpdateFilmographyRequest request) {
        validateActorUser(user);

        Filmography filmography = filmographyRepository.findByIdWithActor(filmographyId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.FILMOGRAPHY_NOT_FOUND));

        // 본인의 필모그래피인지 확인
        if (!filmography.getActor().getUserId().equals(user.getId())) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN, "본인의 필모그래피만 수정할 수 있습니다.");
        }

        filmography.update(
                request.getTitle(),
                request.getYear(),
                request.getType(),
                request.getRole(),
                request.getRoleType(),
                request.getThumbnailUrl(),
                request.getDescription(),
                request.getDirector(),
                request.getProduction()
        );

        Filmography saved = filmographyRepository.save(filmography);
        log.info("Filmography updated: {}", filmographyId);

        return FilmographyResponse.from(saved);
    }

    /**
     * 필모그래피 삭제
     */
    @Transactional
    public void deleteFilmography(User user, UUID filmographyId) {
        validateActorUser(user);

        Filmography filmography = filmographyRepository.findByIdWithActor(filmographyId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.FILMOGRAPHY_NOT_FOUND));

        // 본인의 필모그래피인지 확인
        if (!filmography.getActor().getUserId().equals(user.getId())) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN, "본인의 필모그래피만 삭제할 수 있습니다.");
        }

        filmographyRepository.delete(filmography);
        log.info("Filmography deleted: {}", filmographyId);
    }

    /**
     * 사용자가 배우 타입인지 검증
     */
    private void validateActorUser(User user) {
        if (user.getType() != UserType.ACTOR) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN, "배우 계정만 접근할 수 있습니다.");
        }
    }
}

