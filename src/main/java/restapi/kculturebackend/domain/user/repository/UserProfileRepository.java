package restapi.kculturebackend.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import restapi.kculturebackend.domain.user.entity.UserProfile;

import java.util.Optional;
import java.util.UUID;

/**
 * UserProfile 리포지토리
 */
@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {
    
    Optional<UserProfile> findByUserId(UUID userId);
}

