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
import restapi.kculturebackend.domain.actor.dto.ActorDetailResponse;
import restapi.kculturebackend.domain.actor.dto.ActorSummaryResponse;
import restapi.kculturebackend.domain.actor.dto.UpdateActorProfileRequest;
import restapi.kculturebackend.domain.actor.entity.ActorProfile;
import restapi.kculturebackend.domain.actor.repository.ActorProfileRepository;
import restapi.kculturebackend.domain.user.entity.User;
import restapi.kculturebackend.domain.user.entity.UserType;

import java.util.UUID;

/**
 * 배우 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ActorService {

    private final ActorProfileRepository actorProfileRepository;

    /**
     * 배우 목록 조회 (프로필 완성된 배우만)
     */
    @Transactional(readOnly = true)
    public Page<ActorSummaryResponse> getActors(Pageable pageable) {
        return actorProfileRepository.findByIsProfileCompleteTrue(pageable)
                .map(ActorSummaryResponse::from);
    }

    /**
     * 배우 검색
     */
    @Transactional(readOnly = true)
    public Page<ActorSummaryResponse> searchActors(String name, Pageable pageable) {
        return actorProfileRepository.searchByName(name, pageable)
                .map(ActorSummaryResponse::from);
    }

    /**
     * 배우 상세 조회
     */
    @Transactional(readOnly = true)
    public ActorDetailResponse getActorDetail(UUID actorId) {
        ActorProfile actor = actorProfileRepository.findByUserIdWithUser(actorId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.ACTOR_PROFILE_NOT_FOUND));
        
        return ActorDetailResponse.from(actor);
    }

    /**
     * 내 배우 프로필 조회
     */
    @Transactional(readOnly = true)
    public ActorDetailResponse getMyProfile(User user) {
        validateActorUser(user);
        
        ActorProfile actor = actorProfileRepository.findByUserIdWithUser(user.getId())
                .orElseThrow(() -> new NotFoundException(ErrorCode.ACTOR_PROFILE_NOT_FOUND));
        
        return ActorDetailResponse.from(actor);
    }

    /**
     * 배우 프로필 수정
     */
    @Transactional
    public ActorDetailResponse updateProfile(User user, UpdateActorProfileRequest request) {
        validateActorUser(user);
        
        ActorProfile actor = actorProfileRepository.findById(user.getId())
                .orElseThrow(() -> new NotFoundException(ErrorCode.ACTOR_PROFILE_NOT_FOUND));

        actor.updateProfile(
                request.getStageName(),
                request.getBirthYear(),
                request.getIntroduction(),
                request.getNationality(),
                request.getHeight(),
                request.getWeight(),
                request.getSkills(),
                request.getLanguages(),
                request.getAgency()
        );

        ActorProfile savedActor = actorProfileRepository.save(actor);
        log.info("Actor profile updated: {}", user.getId());
        
        return ActorDetailResponse.from(savedActor);
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

