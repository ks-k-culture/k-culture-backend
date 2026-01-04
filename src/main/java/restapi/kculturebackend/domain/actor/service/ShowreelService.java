package restapi.kculturebackend.domain.actor.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import restapi.kculturebackend.common.exception.ErrorCode;
import restapi.kculturebackend.common.exception.ForbiddenException;
import restapi.kculturebackend.common.exception.NotFoundException;
import restapi.kculturebackend.domain.actor.dto.CreateShowreelRequest;
import restapi.kculturebackend.domain.actor.dto.ShowreelResponse;
import restapi.kculturebackend.domain.actor.dto.UpdateShowreelRequest;
import restapi.kculturebackend.domain.actor.entity.ActorProfile;
import restapi.kculturebackend.domain.actor.entity.Showreel;
import restapi.kculturebackend.domain.actor.repository.ActorProfileRepository;
import restapi.kculturebackend.domain.actor.repository.ShowreelRepository;
import restapi.kculturebackend.domain.user.entity.User;
import restapi.kculturebackend.domain.user.entity.UserType;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 쇼릴 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ShowreelService {

    private final ShowreelRepository showreelRepository;
    private final ActorProfileRepository actorProfileRepository;

    /**
     * 특정 배우의 쇼릴 목록 조회
     */
    @Transactional(readOnly = true)
    public List<ShowreelResponse> getShowreelsByActor(UUID actorId) {
        // 배우 존재 확인
        if (!actorProfileRepository.existsById(actorId)) {
            throw new NotFoundException(ErrorCode.ACTOR_PROFILE_NOT_FOUND);
        }

        return showreelRepository.findByActorId(actorId).stream()
                .map(ShowreelResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 쇼릴 상세 조회 (조회수 증가)
     */
    @Transactional
    public ShowreelResponse getShowreel(UUID showreelId) {
        Showreel showreel = showreelRepository.findByIdWithActor(showreelId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.SHOWREEL_NOT_FOUND));

        showreel.incrementViewCount();
        showreelRepository.save(showreel);

        return ShowreelResponse.from(showreel);
    }

    /**
     * 내 쇼릴 목록 조회
     */
    @Transactional(readOnly = true)
    public List<ShowreelResponse> getMyShowreels(User user) {
        validateActorUser(user);

        return showreelRepository.findByActorId(user.getId()).stream()
                .map(ShowreelResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 인기 쇼릴 조회
     */
    @Transactional(readOnly = true)
    public Page<ShowreelResponse> getPopularShowreels(Pageable pageable) {
        return showreelRepository.findPopular(pageable)
                .map(ShowreelResponse::from);
    }

    /**
     * 쇼릴 생성
     */
    @Transactional
    public ShowreelResponse createShowreel(User user, CreateShowreelRequest request) {
        validateActorUser(user);

        ActorProfile actor = actorProfileRepository.findById(user.getId())
                .orElseThrow(() -> new NotFoundException(ErrorCode.ACTOR_PROFILE_NOT_FOUND));

        Showreel showreel = Showreel.create(
                actor,
                request.getTitle(),
                request.getVideoUrl(),
                request.getThumbnailUrl(),
                request.getDuration(),
                request.getWorkTitle(),
                request.getGenre(),
                request.getDescription(),
                request.getTags()
        );

        Showreel saved = showreelRepository.save(showreel);
        log.info("Showreel created: {} for actor: {}", saved.getId(), user.getId());

        return ShowreelResponse.from(saved);
    }

    /**
     * 쇼릴 수정
     */
    @Transactional
    public ShowreelResponse updateShowreel(User user, UUID showreelId, UpdateShowreelRequest request) {
        validateActorUser(user);

        Showreel showreel = showreelRepository.findByIdWithActor(showreelId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.SHOWREEL_NOT_FOUND));

        // 본인의 쇼릴인지 확인
        if (!showreel.getActor().getUserId().equals(user.getId())) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN, "본인의 쇼릴만 수정할 수 있습니다.");
        }

        showreel.update(
                request.getTitle(),
                request.getVideoUrl(),
                request.getThumbnailUrl(),
                request.getDuration(),
                request.getWorkTitle(),
                request.getGenre(),
                request.getDescription(),
                request.getTags()
        );

        Showreel saved = showreelRepository.save(showreel);
        log.info("Showreel updated: {}", showreelId);

        return ShowreelResponse.from(saved);
    }

    /**
     * 쇼릴 삭제
     */
    @Transactional
    public void deleteShowreel(User user, UUID showreelId) {
        validateActorUser(user);

        Showreel showreel = showreelRepository.findByIdWithActor(showreelId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.SHOWREEL_NOT_FOUND));

        // 본인의 쇼릴인지 확인
        if (!showreel.getActor().getUserId().equals(user.getId())) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN, "본인의 쇼릴만 삭제할 수 있습니다.");
        }

        showreelRepository.delete(showreel);
        log.info("Showreel deleted: {}", showreelId);
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

