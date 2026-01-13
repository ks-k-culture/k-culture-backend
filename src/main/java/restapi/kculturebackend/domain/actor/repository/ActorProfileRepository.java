package restapi.kculturebackend.domain.actor.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import restapi.kculturebackend.domain.actor.entity.ActorProfile;

/**
 * 배우 프로필 레포지토리
 */
public interface ActorProfileRepository extends JpaRepository<ActorProfile, UUID> {

    // 프로필 완성된 배우만 조회
    Page<ActorProfile> findByIsProfileCompleteTrue(Pageable pageable);

    // 활동명으로 검색
    @Query("SELECT a FROM ActorProfile a WHERE " +
           "(:stageName IS NULL OR LOWER(a.stageName) LIKE LOWER(CONCAT('%', :stageName, '%')))")
    Page<ActorProfile> searchByName(@Param("stageName") String stageName, Pageable pageable);

    // 사용자 ID로 조회 (User 정보 fetch join)
    @Query("SELECT a FROM ActorProfile a JOIN FETCH a.user WHERE a.userId = :userId")
    Optional<ActorProfile> findByUserIdWithUser(@Param("userId") UUID userId);
}

