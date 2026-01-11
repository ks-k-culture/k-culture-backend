package restapi.kculturebackend.domain.favorite.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import restapi.kculturebackend.domain.favorite.entity.Favorite;
import restapi.kculturebackend.domain.favorite.entity.FavoriteType;

/**
 * 찜 목록 레포지토리
 */
public interface FavoriteRepository extends JpaRepository<Favorite, UUID> {

    // 사용자의 찜 목록 조회 (페이징)
    Page<Favorite> findByUserId(UUID userId, Pageable pageable);

    // 사용자의 찜 목록 조회 (타입별)
    Page<Favorite> findByUserIdAndType(UUID userId, FavoriteType type, Pageable pageable);

    // 특정 찜 조회 (사용자 + 대상)
    Optional<Favorite> findByUserIdAndTargetIdAndType(UUID userId, UUID targetId, FavoriteType type);

    // 찜 존재 여부 확인
    boolean existsByUserIdAndTargetIdAndType(UUID userId, UUID targetId, FavoriteType type);

    // 사용자의 특정 찜 조회
    @Query("SELECT f FROM Favorite f WHERE f.id = :favoriteId AND f.user.id = :userId")
    Optional<Favorite> findByIdAndUserId(@Param("favoriteId") UUID favoriteId, @Param("userId") UUID userId);
}
