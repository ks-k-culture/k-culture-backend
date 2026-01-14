package restapi.kculturebackend.domain.dashboard.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import restapi.kculturebackend.domain.actor.repository.ActorProfileRepository;
import restapi.kculturebackend.domain.dashboard.dto.ActivityResponse;
import restapi.kculturebackend.domain.dashboard.dto.ActorDashboardStats;
import restapi.kculturebackend.domain.dashboard.dto.AgencyDashboardStats;
import restapi.kculturebackend.domain.dashboard.dto.RecentActivitiesResponse;
import restapi.kculturebackend.domain.dashboard.entity.Activity;
import restapi.kculturebackend.domain.dashboard.entity.ProfileView;
import restapi.kculturebackend.domain.dashboard.repository.ActivityRepository;
import restapi.kculturebackend.domain.dashboard.repository.ContactRequestRepository;
import restapi.kculturebackend.domain.dashboard.repository.ProfileViewRepository;
import restapi.kculturebackend.domain.favorite.entity.FavoriteType;
import restapi.kculturebackend.domain.favorite.repository.FavoriteRepository;
import restapi.kculturebackend.domain.project.repository.CharacterRepository;
import restapi.kculturebackend.domain.project.repository.ProjectRepository;
import restapi.kculturebackend.domain.user.entity.User;
import restapi.kculturebackend.domain.user.entity.UserType;
import restapi.kculturebackend.domain.user.repository.UserRepository;

/**
 * 대시보드 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardService {

    private static final int RECENT_ACTIVITIES_LIMIT = 10;
    private static final int VIEW_COOLDOWN_HOURS = 1;

    private final ActorProfileRepository actorProfileRepository;
    private final ProjectRepository projectRepository;
    private final CharacterRepository characterRepository;
    private final FavoriteRepository favoriteRepository;
    private final ProfileViewRepository profileViewRepository;
    private final ContactRequestRepository contactRequestRepository;
    private final ActivityRepository activityRepository;
    private final UserRepository userRepository;

    /**
     * 배우 대시보드 통계 조회
     */
    @Transactional(readOnly = true)
    public ActorDashboardStats getActorStats(User user) {
        int profileCompleteness = calculateProfileCompleteness(user);

        // 프로필 조회수 조회
        long profileViews = profileViewRepository.countByActorId(user.getId());

        // 찜 받은 수 (내 프로필이 다른 사람에게 찜된 수)
        long likes = favoriteRepository.countByTargetIdAndType(user.getId(), FavoriteType.ACTOR);

        // 섭외 요청 수 조회
        long contactRequests = contactRequestRepository.countByActorId(user.getId());

        // 최근 활동 내역 조회
        List<Activity> activities = activityRepository.findRecentByUserId(
                user.getId(), PageRequest.of(0, RECENT_ACTIVITIES_LIMIT));
        List<ActivityResponse> recentActivities = activities.stream()
                .map(ActivityResponse::from)
                .toList();

        return ActorDashboardStats.builder()
                .profileViews((int) profileViews)
                .likes((int) likes)
                .contactRequests((int) contactRequests)
                .profileCompleteness(profileCompleteness)
                .recentActivities(recentActivities)
                .build();
    }

    /**
     * 에이전시 대시보드 통계 조회
     */
    @Transactional(readOnly = true)
    public AgencyDashboardStats getAgencyStats(User user) {
        // 진행중 프로젝트 수
        long activeProjects = projectRepository.countActiveProjectsByAgencyUserId(user.getId());

        // 찜한 배우 수
        long favoriteActors = favoriteRepository.findByUserIdAndType(
                user.getId(), FavoriteType.ACTOR, PageRequest.of(0, 1000)).getTotalElements();

        // 보낸 섭외 요청 수
        long sentContacts = contactRequestRepository.countByAgencyId(user.getId());

        // 전체 캐릭터 수
        long totalCharacters = characterRepository.countByAgencyUserId(user.getId());

        return AgencyDashboardStats.builder()
                .activeProjects((int) activeProjects)
                .favoriteActors((int) favoriteActors)
                .sentContacts((int) sentContacts)
                .totalCharacters((int) totalCharacters)
                .build();
    }

    /**
     * 사용자 타입 확인
     */
    public boolean isActor(User user) {
        return user.getType() == UserType.ACTOR;
    }

    /**
     * 프로필 조회수 기록
     */
    @Transactional
    public void recordProfileView(UUID actorId, User viewer, String viewerIp) {
        User actor = userRepository.findById(actorId).orElse(null);
        if (actor == null || actor.getType() != UserType.ACTOR) {
            log.warn("Invalid actor id for profile view: {}", actorId);
            return;
        }

        // 본인 프로필 조회는 기록하지 않음
        if (viewer != null && viewer.getId().equals(actorId)) {
            return;
        }

        LocalDateTime cooldownTime = LocalDateTime.now().minusHours(VIEW_COOLDOWN_HOURS);

        // 중복 조회 체크 (로그인 사용자)
        if (viewer != null) {
            boolean recentlyViewed = profileViewRepository.existsByActorIdAndViewerIdSince(
                    actorId, viewer.getId(), cooldownTime);
            if (recentlyViewed) {
                return;
            }
        } else if (viewerIp != null) {
            // 중복 조회 체크 (비로그인 사용자 - IP 기반)
            boolean recentlyViewed = profileViewRepository.existsByActorIdAndViewerIpSince(
                    actorId, viewerIp, cooldownTime);
            if (recentlyViewed) {
                return;
            }
        }

        // 조회 기록 저장
        ProfileView profileView = ProfileView.create(actor, viewer, viewerIp);
        profileViewRepository.save(profileView);

        // 활동 내역 기록
        Activity activity = Activity.profileViewed(actor, viewer);
        activityRepository.save(activity);

        log.info("Profile view recorded for actor: {}", actorId);
    }

    /**
     * 찜 활동 기록
     */
    @Transactional
    public void recordFavoriteActivity(UUID actorId, User favoritedBy) {
        User actor = userRepository.findById(actorId).orElse(null);
        if (actor == null) {
            return;
        }

        Activity activity = Activity.favorited(actor, favoritedBy);
        activityRepository.save(activity);
    }

    /**
     * 최근 활동 내역 조회
     */
    @Transactional(readOnly = true)
    public RecentActivitiesResponse getRecentActivities(User user, int limit) {
        List<Activity> activities = activityRepository.findRecentByUserId(
                user.getId(), PageRequest.of(0, Math.min(limit, 50)));

        List<ActivityResponse> responses = activities.stream()
                .map(ActivityResponse::from)
                .toList();

        int total = (int) activityRepository.countByUserId(user.getId());

        return RecentActivitiesResponse.of(responses, total);
    }

    /**
     * 프로필 완성도 계산
     */
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
