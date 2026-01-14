package restapi.kculturebackend.domain.dashboard.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import restapi.kculturebackend.domain.dashboard.entity.ContactRequest;
import restapi.kculturebackend.domain.dashboard.entity.ContactRequestStatus;

/**
 * 섭외 요청 레포지토리
 */
public interface ContactRequestRepository extends JpaRepository<ContactRequest, UUID> {

    //특정 배우가 받은 섭외 요청 수 조회
    long countByActorId(UUID actorId);

    //특정 배우가 받은 특정 상태의 섭외 요청 수 조회
    long countByActorIdAndStatus(UUID actorId, ContactRequestStatus status);

    //특정 에이전시가 보낸 섭외 요청 수 조회
    long countByAgencyId(UUID agencyId);

    //배우가 받은 섭외 요청 목록 조회 (페이징)
    Page<ContactRequest> findByActorIdOrderByCreatedAtDesc(UUID actorId, Pageable pageable);

    //에이전시가 보낸 섭외 요청 목록 조회 (페이징)
    Page<ContactRequest> findByAgencyIdOrderByCreatedAtDesc(UUID agencyId, Pageable pageable);

    //특정 섭외 요청 조회 (배우용)
    @Query("SELECT cr FROM ContactRequest cr WHERE cr.id = :requestId AND cr.actor.id = :actorId")
    Optional<ContactRequest> findByIdAndActorId(@Param("requestId") UUID requestId, @Param("actorId") UUID actorId);

    //특정 섭외 요청 조회 (에이전시용)
    @Query("SELECT cr FROM ContactRequest cr WHERE cr.id = :requestId AND cr.agency.id = :agencyId")
    Optional<ContactRequest> findByIdAndAgencyId(@Param("requestId") UUID requestId, @Param("agencyId") UUID agencyId);

    //중복 섭외 요청 확인
    boolean existsByAgencyIdAndActorIdAndProjectIdAndStatusIn(
            UUID agencyId, UUID actorId, UUID projectId, List<ContactRequestStatus> statuses);
}
