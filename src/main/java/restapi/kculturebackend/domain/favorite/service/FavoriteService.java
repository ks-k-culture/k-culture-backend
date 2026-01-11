package restapi.kculturebackend.domain.favorite.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import restapi.kculturebackend.common.exception.ConflictException;
import restapi.kculturebackend.common.exception.ErrorCode;
import restapi.kculturebackend.common.exception.NotFoundException;
import restapi.kculturebackend.domain.actor.dto.ActorSummaryResponse;
import restapi.kculturebackend.domain.actor.repository.ActorProfileRepository;
import restapi.kculturebackend.domain.favorite.dto.CreateFavoriteRequest;
import restapi.kculturebackend.domain.favorite.dto.FavoriteResponse;
import restapi.kculturebackend.domain.favorite.entity.Favorite;
import restapi.kculturebackend.domain.favorite.entity.FavoriteType;
import restapi.kculturebackend.domain.favorite.repository.FavoriteRepository;
import restapi.kculturebackend.domain.user.entity.User;

import java.util.UUID;

/**
 * 찜 목록 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final ActorProfileRepository actorProfileRepository;

    /**
     * 찜 목록 조회
     */
    @Transactional(readOnly = true)
    public Page<FavoriteResponse> getFavorites(User user, FavoriteType type, Pageable pageable) {
        Page<Favorite> favorites;

        if (type != null) {
            favorites = favoriteRepository.findByUserIdAndType(user.getId(), type, pageable);
        } else {
            favorites = favoriteRepository.findByUserId(user.getId(), pageable);
        }

        return favorites.map(favorite -> {
            if (favorite.getType() == FavoriteType.ACTOR) {
                return actorProfileRepository.findByUserIdWithUser(favorite.getTargetId())
                        .map(actor -> FavoriteResponse.from(favorite, ActorSummaryResponse.from(actor)))
                        .orElse(FavoriteResponse.from(favorite));
            }
            return FavoriteResponse.from(favorite);
        });
    }

    /**
     * 찜 추가
     */
    @Transactional
    public FavoriteResponse addFavorite(User user, CreateFavoriteRequest request) {
        // 중복 체크
        if (favoriteRepository.existsByUserIdAndTargetIdAndType(user.getId(), request.getTargetId(), request.getType())) {
            throw new ConflictException(ErrorCode.FAVORITE_ALREADY_EXISTS);
        }

        // 대상 존재 확인
        validateTargetExists(request.getTargetId(), request.getType());

        Favorite favorite = Favorite.create(user, request.getTargetId(), request.getType());
        Favorite saved = favoriteRepository.save(favorite);

        log.info("Favorite added: user={}, target={}, type={}", user.getId(), request.getTargetId(), request.getType());

        return FavoriteResponse.from(saved);
    }

    /**
     * 찜 삭제
     */
    @Transactional
    public void deleteFavorite(User user, UUID favoriteId) {
        Favorite favorite = favoriteRepository.findByIdAndUserId(favoriteId, user.getId())
                .orElseThrow(() -> new NotFoundException(ErrorCode.FAVORITE_NOT_FOUND));

        favoriteRepository.delete(favorite);
        log.info("Favorite deleted: user={}, favoriteId={}", user.getId(), favoriteId);
    }

    /**
     * 대상 존재 확인
     */
    private void validateTargetExists(UUID targetId, FavoriteType type) {
        boolean exists = switch (type) {
            case ACTOR -> actorProfileRepository.existsById(targetId);
            case PROJECT -> true; // TODO: ProjectRepository 연동 필요
        };

        if (!exists) {
            throw new NotFoundException(ErrorCode.RESOURCE_NOT_FOUND);
        }
    }
}
