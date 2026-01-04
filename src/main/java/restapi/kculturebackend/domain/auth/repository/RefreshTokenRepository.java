package restapi.kculturebackend.domain.auth.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import restapi.kculturebackend.domain.auth.entity.RefreshToken;

import java.util.Optional;

/**
 * RefreshToken Redis 리포지토리
 */
@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
    
    Optional<RefreshToken> findByToken(String token);
    
    void deleteByToken(String token);
}

