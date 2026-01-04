package restapi.kculturebackend.domain.actor.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import restapi.kculturebackend.domain.actor.entity.Showreel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 쇼릴 리포지토리
 */
@Repository
public interface ShowreelRepository extends JpaRepository<Showreel, UUID> {

    /**
     * 특정 배우의 쇼릴 목록 조회 (최신순)
     */
    @Query("SELECT s FROM Showreel s WHERE s.actor.userId = :actorId ORDER BY s.createdAt DESC")
    List<Showreel> findByActorId(@Param("actorId") UUID actorId);

    /**
     * 특정 배우의 쇼릴 페이징 조회
     */
    @Query("SELECT s FROM Showreel s WHERE s.actor.userId = :actorId")
    Page<Showreel> findByActorId(@Param("actorId") UUID actorId, Pageable pageable);

    /**
     * 쇼릴 상세 조회 (배우 정보 함께)
     */
    @Query("SELECT s FROM Showreel s JOIN FETCH s.actor a JOIN FETCH a.user WHERE s.id = :id")
    Optional<Showreel> findByIdWithActor(@Param("id") UUID id);

    /**
     * 특정 배우의 쇼릴 개수
     */
    @Query("SELECT COUNT(s) FROM Showreel s WHERE s.actor.userId = :actorId")
    long countByActorId(@Param("actorId") UUID actorId);

    /**
     * 인기 쇼릴 조회 (조회수 기준)
     */
    @Query("SELECT s FROM Showreel s JOIN FETCH s.actor ORDER BY s.viewCount DESC")
    Page<Showreel> findPopular(Pageable pageable);
}

