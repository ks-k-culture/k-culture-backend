package restapi.kculturebackend.domain.actor.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import restapi.kculturebackend.domain.actor.entity.Filmography;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 필모그래피 리포지토리
 */
@Repository
public interface FilmographyRepository extends JpaRepository<Filmography, UUID> {

    /**
     * 특정 배우의 필모그래피 목록 조회 (최신순)
     */
    @Query("SELECT f FROM Filmography f WHERE f.actor.userId = :actorId ORDER BY f.year DESC, f.createdAt DESC")
    List<Filmography> findByActorId(@Param("actorId") UUID actorId);

    /**
     * 특정 배우의 필모그래피 페이징 조회
     */
    @Query("SELECT f FROM Filmography f WHERE f.actor.userId = :actorId")
    Page<Filmography> findByActorId(@Param("actorId") UUID actorId, Pageable pageable);

    /**
     * 필모그래피 상세 조회 (배우 정보 함께)
     */
    @Query("SELECT f FROM Filmography f JOIN FETCH f.actor a JOIN FETCH a.user WHERE f.id = :id")
    Optional<Filmography> findByIdWithActor(@Param("id") UUID id);

    /**
     * 특정 배우의 필모그래피 개수
     */
    @Query("SELECT COUNT(f) FROM Filmography f WHERE f.actor.userId = :actorId")
    long countByActorId(@Param("actorId") UUID actorId);
}

