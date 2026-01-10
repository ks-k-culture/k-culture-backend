package restapi.kculturebackend.domain.project.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import restapi.kculturebackend.domain.project.entity.Project;
import restapi.kculturebackend.domain.project.entity.ProjectStatus;
import restapi.kculturebackend.domain.project.entity.ProjectType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
    @Query("SELECT p FROM Project p WHERE " +
           "(:name IS NULL OR LOWER(p.projectName) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:type IS NULL OR p.projectType = :type) AND " +
           "(:status IS NULL OR p.status = :status)")
    Page<Project> search(@Param("name") String name,
                         @Param("type") ProjectType type,
                         @Param("status") ProjectStatus status,
                         Pageable pageable);
}

