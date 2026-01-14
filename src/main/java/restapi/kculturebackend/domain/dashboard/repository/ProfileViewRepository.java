package restapi.kculturebackend.domain.dashboard.repository;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import restapi.kculturebackend.domain.dashboard.entity.ProfileView;

/**
 * 프로필 조회 기록 레포지토리
 */
public interface ProfileViewRepository extends JpaRepository<ProfileView, UUID> {

    
    //특정 배우의 총 프로필 조회수 조회
    long countByActorId(UUID actorId);

    //특정 기간 내 배우의 프로필 조회수 조회
    @Query("SELECT COUNT(pv) FROM ProfileView pv WHERE pv.actor.id = :actorId AND pv.createdAt >= :since")
    long countByActorIdSince(@Param("actorId") UUID actorId, @Param("since") LocalDateTime since);

    //중복 조회 방지를 위한 최근 조회 기록 확인
    //동일 viewer가 동일 actor를 일정 시간 내에 조회했는지 확인
    @Query("SELECT COUNT(pv) > 0 FROM ProfileView pv " +
           "WHERE pv.actor.id = :actorId AND pv.viewer.id = :viewerId AND pv.createdAt >= :since")
    boolean existsByActorIdAndViewerIdSince(
            @Param("actorId") UUID actorId,
            @Param("viewerId") UUID viewerId,
            @Param("since") LocalDateTime since);

    //IP 기반 중복 조회 확인 (비로그인 사용자)
    @Query("SELECT COUNT(pv) > 0 FROM ProfileView pv " +
           "WHERE pv.actor.id = :actorId AND pv.viewerIp = :viewerIp AND pv.createdAt >= :since")
    boolean existsByActorIdAndViewerIpSince(
            @Param("actorId") UUID actorId,
            @Param("viewerIp") String viewerIp,
            @Param("since") LocalDateTime since);
}
