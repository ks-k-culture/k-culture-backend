package restapi.kculturebackend.domain.agency.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import restapi.kculturebackend.domain.agency.entity.AgencyProfile;

import java.util.Optional;
import java.util.UUID;

/**
 * 에이전시 프로필 레포지토리
 */
public interface AgencyProfileRepository extends JpaRepository<AgencyProfile, UUID> {

    /**
     * 프로필 완성된 에이전시만 조회
     */
    Page<AgencyProfile> findByIsProfileCompleteTrue(Pageable pageable);

    /**
     * 에이전시명으로 검색
     */
    @Query("SELECT a FROM AgencyProfile a WHERE " +
           "(:name IS NULL OR LOWER(a.agencyName) LIKE LOWER(CONCAT('%', :name, '%')))")
    Page<AgencyProfile> searchByName(@Param("name") String name, Pageable pageable);

    /**
     * 사용자 ID로 조회 (User 정보 fetch join)
     */
    @Query("SELECT a FROM AgencyProfile a JOIN FETCH a.user WHERE a.userId = :userId")
    Optional<AgencyProfile> findByUserIdWithUser(@Param("userId") UUID userId);
}

