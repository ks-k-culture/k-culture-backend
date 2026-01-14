package restapi.kculturebackend.domain.dashboard.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import restapi.kculturebackend.domain.dashboard.entity.Activity;
import restapi.kculturebackend.domain.dashboard.entity.ActivityType;

/**
 * 활동 내역 레포지토리
 */
public interface ActivityRepository extends JpaRepository<Activity, UUID> {

    //특정 사용자의 최근 활동 내역 조회 (페이징)
    Page<Activity> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    //특정 사용자의 최근 N개 활동 내역 조회
    @Query("SELECT a FROM Activity a WHERE a.user.id = :userId ORDER BY a.createdAt DESC")
    List<Activity> findRecentByUserId(@Param("userId") UUID userId, Pageable pageable);

    //특정 사용자의 특정 타입 활동 내역 조회
    Page<Activity> findByUserIdAndTypeOrderByCreatedAtDesc(UUID userId, ActivityType type, Pageable pageable);

    //특정 사용자의 활동 수 조회
    long countByUserId(UUID userId);
}
