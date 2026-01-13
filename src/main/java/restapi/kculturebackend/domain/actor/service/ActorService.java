package restapi.kculturebackend.domain.actor.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import restapi.kculturebackend.common.exception.ErrorCode;
import restapi.kculturebackend.common.exception.ForbiddenException;
import restapi.kculturebackend.common.exception.NotFoundException;
import restapi.kculturebackend.domain.actor.dto.ActorDetailResponse;
import restapi.kculturebackend.domain.actor.dto.ActorRecommendRequest;
import restapi.kculturebackend.domain.actor.dto.ActorRecommendResponse;
import restapi.kculturebackend.domain.actor.dto.ActorSummaryResponse;
import restapi.kculturebackend.domain.actor.dto.ContactActorRequest;
import restapi.kculturebackend.domain.actor.dto.CreateActorProfileRequest;
import restapi.kculturebackend.domain.actor.dto.UpdateActorProfileRequest;
import restapi.kculturebackend.domain.actor.entity.ActorProfile;
import restapi.kculturebackend.domain.actor.repository.ActorProfileRepository;
import restapi.kculturebackend.domain.user.entity.User;
import restapi.kculturebackend.domain.user.entity.UserType;
import restapi.kculturebackend.domain.user.repository.UserRepository;

/**
 * 배우 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ActorService {

    private final ActorProfileRepository actorProfileRepository;
    private final UserRepository userRepository;

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
        
        // Lazy 컬렉션 초기화 (트랜잭션 내에서 수행)
        actor.getSkills().size();
        actor.getLanguages().size();
        
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
        
        // Lazy 컬렉션 초기화 (트랜잭션 내에서 수행)
        actor.getSkills().size();
        actor.getLanguages().size();
        
        return ActorDetailResponse.from(actor);
    }

    // 배우 프로필 수정
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

    // 프로필 이미지 업데이트
    @Transactional
    public void updateProfileImage(User user, String imageUrl) {
        validateActorUser(user);
        
        // User 엔티티를 다시 조회하여 영속성 컨텍스트에 포함시킴
        User managedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
        
        managedUser.updateProfileImage(imageUrl);
        userRepository.save(managedUser);
        
        log.info("Profile image updated for user: {}", user.getId());
    }

    // 배우 프로필 등록
    @Transactional
    public ActorDetailResponse createProfile(User user, CreateActorProfileRequest request, String profileImageUrl) {
        validateActorUser(user);

        ActorProfile actor = actorProfileRepository.findById(user.getId())
                .orElseThrow(() -> new NotFoundException(ErrorCode.ACTOR_PROFILE_NOT_FOUND));

        // 프로필 이미지 업데이트
        if (profileImageUrl != null) {
            user.updateProfile(request.getName(), profileImageUrl);
        } else {
            user.updateProfile(request.getName(), user.getProfileImage());
        }

        // birthYear가 직접 전달되면 사용, 아니면 나이대에서 추정
        Integer birthYear = request.getBirthYear() != null 
                ? request.getBirthYear() 
                : estimateBirthYear(request.getAgeGroup());

        actor.updateProfile(
                request.getName(),
                birthYear,
                request.getIntroduction(),
                null, // nationality
                request.getHeight(),
                request.getWeight(),
                request.getSkills(),
                null, // languages
                null  // agency
        );

        ActorProfile saved = actorProfileRepository.save(actor);
        log.info("Actor profile created: {}", user.getId());

        return ActorDetailResponse.from(saved);
    }

    // AI 배우 추천 (현재는 간단한 필터링으로 구현, 추후 AI 서비스 연동)
    @Transactional(readOnly = true)
    public List<ActorRecommendResponse> recommendActors(User user, ActorRecommendRequest request) {
        validateAgencyUser(user);

        // 현재는 프로필 완성된 배우 중 상위 10명 반환 (추후 AI 로직 추가)
        Page<ActorProfile> actors = actorProfileRepository.findByIsProfileCompleteTrue(PageRequest.of(0, 10));

        List<ActorRecommendResponse> recommendations = new ArrayList<>();
        int baseScore = 85;

        for (ActorProfile actor : actors) {
            List<String> reasons = new ArrayList<>();
            reasons.add("프로필 완성도 높음");
            if (actor.getSkills() != null && !actor.getSkills().isEmpty()) {
                reasons.add("다양한 스킬 보유");
            }

            recommendations.add(ActorRecommendResponse.from(actor, baseScore, reasons));
            baseScore -= 5;
        }

        return recommendations;
    }

    // 배우 연락하기
    @Transactional
    public UUID contactActor(User user, UUID actorId, ContactActorRequest request) {
        validateAgencyUser(user);

        // 배우 존재 확인
        if (!actorProfileRepository.existsById(actorId)) {
            throw new NotFoundException(ErrorCode.ACTOR_PROFILE_NOT_FOUND);
        }

        // TODO: 실제로는 Contact 엔티티를 생성하고 알림을 보내야 함
        // 현재는 UUID만 생성하여 반환
        UUID contactId = UUID.randomUUID();
        log.info("Contact request sent from agency {} to actor {}", user.getId(), actorId);

        return contactId;
    }

    // 나이대 문자열에서 출생년도 추정
    private Integer estimateBirthYear(String ageGroup) {
        int currentYear = java.time.Year.now().getValue();
        return switch (ageGroup) {
            case "10대" -> currentYear - 15;
            case "20대" -> currentYear - 25;
            case "30대" -> currentYear - 35;
            case "40대" -> currentYear - 45;
            case "50대" -> currentYear - 55;
            case "60대 이상" -> currentYear - 65;
            default -> null;
        };
    }

    // 사용자가 배우 타입인지 검증
    private void validateActorUser(User user) {
        if (user.getType() != UserType.ACTOR) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN, "배우 계정만 접근할 수 있습니다.");
        }
    }

    // 사용자가 에이전시 타입인지 검증
    private void validateAgencyUser(User user) {
        if (user.getType() != UserType.AGENCY) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN, "에이전시 계정만 접근할 수 있습니다.");
        }
    }
}

