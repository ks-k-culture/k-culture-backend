package restapi.kculturebackend.domain.actor.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import restapi.kculturebackend.domain.actor.dto.ActorSearchRequest;
import restapi.kculturebackend.domain.actor.entity.ActorProfile;

/**
 * 배우 프로필 레포지토리 커스텀 인터페이스
 * 복잡한 검색 쿼리를 위한 인터페이스
 */
public interface ActorProfileRepositoryCustom {

    /**
     * 고급 검색 쿼리
     * 필터링, 정렬, 페이징을 지원하는 복합 검색
     */
    Page<ActorProfile> searchWithFilters(ActorSearchRequest request, Pageable pageable);
}
