package restapi.kculturebackend.domain.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import restapi.kculturebackend.domain.project.entity.Character;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 캐릭터 리포지토리
 */
@Repository
public interface CharacterRepository extends JpaRepository<Character, UUID> {

    /**
     * 특정 프로젝트의 캐릭터 목록
     */
    @Query("SELECT c FROM Character c WHERE c.project.id = :projectId ORDER BY c.createdAt ASC")
    List<Character> findByProjectId(@Param("projectId") UUID projectId);

    /**
     * 캐릭터 상세 조회 (프로젝트 정보 함께)
     */
    @Query("SELECT c FROM Character c JOIN FETCH c.project p WHERE c.id = :id")
    Optional<Character> findByIdWithProject(@Param("id") UUID id);

    /**
     * 캐스팅 미완료 캐릭터만 조회
     */
    @Query("SELECT c FROM Character c WHERE c.project.id = :projectId AND c.isCastingComplete = false")
    List<Character> findOpenCastings(@Param("projectId") UUID projectId);

    /**
     * 프로젝트의 캐릭터 수
     */
    @Query("SELECT COUNT(c) FROM Character c WHERE c.project.id = :projectId")
    long countByProjectId(@Param("projectId") UUID projectId);

    /**
     * 에이전시별 전체 캐릭터 수
     */
    @Query("SELECT COUNT(c) FROM Character c WHERE c.project.agency.userId = :userId")
    long countByAgencyUserId(@Param("userId") UUID userId);
}

