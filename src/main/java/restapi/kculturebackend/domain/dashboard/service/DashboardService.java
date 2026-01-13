package restapi.kculturebackend.domain.dashboard.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import restapi.kculturebackend.domain.actor.repository.ActorProfileRepository;
import restapi.kculturebackend.domain.dashboard.dto.ActorDashboardStats;
import restapi.kculturebackend.domain.dashboard.dto.AgencyDashboardStats;
import restapi.kculturebackend.domain.favorite.entity.FavoriteType;
import restapi.kculturebackend.domain.favorite.repository.FavoriteRepository;
import restapi.kculturebackend.domain.project.repository.CharacterRepository;
import restapi.kculturebackend.domain.project.repository.ProjectRepository;
import restapi.kculturebackend.domain.user.entity.User;
import restapi.kculturebackend.domain.user.entity.UserType;

/**
 * 대시보드 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ActorProfileRepository actorProfileRepository;
    private final ProjectRepository projectRepository;
    private final CharacterRepository characterRepository;
    private final FavoriteRepository favoriteRepository;

    // 배우 대시보드 통계 조회
    @Transactional(readOnly = true)
    public ActorDashboardStats getActorStats(User user) {
        int profileCompleteness = calculateProfileCompleteness(user);

        // 찜 받은 수 (내 프로필이 다른 사람에게 찜된 수)
        long likes = favoriteRepository.countByTargetIdAndType(user.getId(), FavoriteType.ACTOR);

        return ActorDashboardStats.builder()
                .profileViews(0) // TODO: 조회수 트래킹 테이블 생성 후 구현
                .likes((int) likes)
                .contactRequests(0) // TODO: Contact 테이블 생성 후 구현
                .profileCompleteness(profileCompleteness)
                .build();
    }

    // 에이전시 대시보드 통계 조회
    @Transactional(readOnly = true)
    public AgencyDashboardStats getAgencyStats(User user) {
        // 진행중 프로젝트 수
        long activeProjects = projectRepository.countActiveProjectsByAgencyUserId(user.getId());

        // 찜한 배우 수
        long favoriteActors = favoriteRepository.findByUserIdAndType(user.getId(), FavoriteType.ACTOR, PageRequest.of(0, 1000)).getTotalElements();

        // 전체 캐릭터 수
        long totalCharacters = characterRepository.countByAgencyUserId(user.getId());

        return AgencyDashboardStats.builder()
                .activeProjects((int) activeProjects)
                .favoriteActors((int) favoriteActors)
                .sentContacts(0) // TODO: Contact 테이블 생성 후 구현
                .totalCharacters((int) totalCharacters)
                .build();
    }

    // 사용자 타입 확인
    public boolean isActor(User user) {
        return user.getType() == UserType.ACTOR;
    }

    // 프로필 완성도 계산
    private int calculateProfileCompleteness(User user) {
        return actorProfileRepository.findById(user.getId())
                .map(actor -> {
                    int score = 0;
                    if (actor.getStageName() != null) score += 15;
                    if (actor.getBirthYear() != null) score += 10;
                    if (actor.getIntroduction() != null) score += 20;
                    if (actor.getHeight() != null) score += 10;
                    if (actor.getWeight() != null) score += 10;
                    if (actor.getSkills() != null && !actor.getSkills().isEmpty()) score += 15;
                    if (actor.getLanguages() != null && !actor.getLanguages().isEmpty()) score += 10;
                    if (actor.getUser().getProfileImage() != null) score += 10;
                    return score;
                })
                .orElse(0);
    }
}
