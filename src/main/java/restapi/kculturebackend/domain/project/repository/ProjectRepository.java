package restapi.kculturebackend.domain.project.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import restapi.kculturebackend.domain.project.entity.Project;

/**
 * 프로젝트 리포지토리
 */
@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {

    /**
     * 특정 에이전시의 프로젝트 목록
     */
    @Query("SELECT p FROM Project p WHERE p.agency.userId = :agencyId ORDER BY p.createdAt DESC")
    List<Project> findByAgencyId(@Param("agencyId") UUID agencyId);

    /**
     * 특정 에이전시의 프로젝트 페이징 조회
     */
    @Query("SELECT p FROM Project p WHERE p.agency.userId = :agencyId")
    Page<Project> findByAgencyId(@Param("agencyId") UUID agencyId, Pageable pageable);

    /**
     * 프로젝트 상세 조회 (에이전시 정보 함께)
     */
    @Query("SELECT p FROM Project p JOIN FETCH p.agency a JOIN FETCH a.user WHERE p.id = :id")
    Optional<Project> findByIdWithAgency(@Param("id") UUID id);

    /**
     * 캐스팅 중인 프로젝트 목록
     */
    @Query("SELECT p FROM Project p WHERE p.status = 'CASTING' ORDER BY p.createdAt DESC")
    Page<Project> findCastingProjects(Pageable pageable);

    /**
     * 프로젝트 검색 (이름, 유형, 상태 필터)
     */
    @Query(value = "SELECT * FROM projects p WHERE " +
           "(:name IS NULL OR p.project_name ILIKE CONCAT('%', :name, '%')) AND " +
           "(:type IS NULL OR p.project_type = :type) AND " +
           "(:status IS NULL OR p.status = :status) " +
           "ORDER BY p.created_at DESC",
           countQuery = "SELECT COUNT(*) FROM projects p WHERE " +
           "(:name IS NULL OR p.project_name ILIKE CONCAT('%', :name, '%')) AND " +
           "(:type IS NULL OR p.project_type = :type) AND " +
           "(:status IS NULL OR p.status = :status)",
           nativeQuery = true)
    Page<Project> search(@Param("name") String name,
                         @Param("type") String type,
                         @Param("status") String status,
                         Pageable pageable);

    /**
     * 에이전시별 진행중 프로젝트 수
     */
    @Query("SELECT COUNT(p) FROM Project p WHERE p.agency.userId = :userId AND p.status IN ('CASTING', 'PRE_PRODUCTION', 'IN_PRODUCTION')")
    long countActiveProjectsByAgencyUserId(@Param("userId") UUID userId);
}

