package restapi.kculturebackend.domain.actor.repository;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import restapi.kculturebackend.domain.actor.dto.ActorSearchRequest;
import restapi.kculturebackend.domain.actor.entity.ActorCategory;
import restapi.kculturebackend.domain.actor.entity.ActorProfile;
import restapi.kculturebackend.domain.actor.entity.Gender;

/**
 * 배우 프로필 레포지토리 커스텀 구현
 * Criteria API를 사용한 동적 쿼리 생성
 */
@Repository
public class ActorProfileRepositoryImpl implements ActorProfileRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<ActorProfile> searchWithFilters(ActorSearchRequest request, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        
        // 메인 쿼리
        CriteriaQuery<ActorProfile> query = cb.createQuery(ActorProfile.class);
        Root<ActorProfile> actor = query.from(ActorProfile.class);
        actor.fetch("user", JoinType.LEFT);

        List<Predicate> predicates = buildPredicates(cb, actor, request);
        
        query.where(predicates.toArray(new Predicate[0]));
        query.orderBy(buildOrder(cb, actor, request, pageable));

        TypedQuery<ActorProfile> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        List<ActorProfile> resultList = typedQuery.getResultList();
        long total = countWithFilters(request);

        return new PageImpl<>(resultList, pageable, total);
    }

    private List<Predicate> buildPredicates(CriteriaBuilder cb, Root<ActorProfile> actor, ActorSearchRequest request) {
        List<Predicate> predicates = new ArrayList<>();
        int currentYear = Year.now().getValue();

        // 프로필 완성 필수
        predicates.add(cb.isTrue(actor.get("isProfileComplete")));

        // 구분 필터 (배우/모델)
        if (StringUtils.hasText(request.getCategory()) && !"무관".equals(request.getCategory())) {
            ActorCategory category = ActorCategory.fromDisplayName(request.getCategory());
            if (category != null) {
                predicates.add(cb.equal(actor.get("category"), category));
            }
        }

        // 성별 필터
        if (StringUtils.hasText(request.getGender()) && !"무관".equals(request.getGender())) {
            Gender gender = Gender.fromDisplayName(request.getGender());
            if (gender != null) {
                predicates.add(cb.equal(actor.get("gender"), gender));
            }
        }

        // 나이 필터 (birthYear를 나이로 변환하여 비교)
        if (request.getAgeMin() != null) {
            int maxBirthYear = currentYear - request.getAgeMin();
            predicates.add(cb.lessThanOrEqualTo(actor.get("birthYear"), maxBirthYear));
        }
        if (request.getAgeMax() != null) {
            int minBirthYear = currentYear - request.getAgeMax();
            predicates.add(cb.greaterThanOrEqualTo(actor.get("birthYear"), minBirthYear));
        }

        // 키 필터
        if (request.getHeightMin() != null) {
            predicates.add(cb.greaterThanOrEqualTo(actor.get("height"), request.getHeightMin()));
        }
        if (request.getHeightMax() != null) {
            predicates.add(cb.lessThanOrEqualTo(actor.get("height"), request.getHeightMax()));
        }

        // 몸무게 필터
        if (request.getWeightMin() != null) {
            predicates.add(cb.greaterThanOrEqualTo(actor.get("weight"), request.getWeightMin()));
        }
        if (request.getWeightMax() != null) {
            predicates.add(cb.lessThanOrEqualTo(actor.get("weight"), request.getWeightMax()));
        }

        // 키워드 검색 (이름, 활동명)
        if (StringUtils.hasText(request.getKeyword())) {
            String keyword = "%" + request.getKeyword().toLowerCase() + "%";
            Predicate stageNameLike = cb.like(cb.lower(actor.get("stageName")), keyword);
            Predicate nameLike = cb.like(cb.lower(actor.get("user").get("name")), keyword);
            predicates.add(cb.or(stageNameLike, nameLike));
        }

        return predicates;
    }

    private List<Order> buildOrder(CriteriaBuilder cb, Root<ActorProfile> actor, 
                                   ActorSearchRequest request, Pageable pageable) {
        List<Order> orders = new ArrayList<>();
        
        String sortBy = request.getSortBy();
        boolean isDesc = !"asc".equalsIgnoreCase(request.getSortDirection());

        if (StringUtils.hasText(sortBy)) {
            switch (sortBy) {
                case "views_high":
                    orders.add(cb.desc(actor.get("viewCount")));
                    break;
                case "views_low":
                    orders.add(cb.asc(actor.get("viewCount")));
                    break;
                case "name":
                    orders.add(isDesc ? cb.desc(actor.get("stageName")) : cb.asc(actor.get("stageName")));
                    break;
                case "age_young":
                    orders.add(cb.desc(actor.get("birthYear"))); // 출생년도 높을수록 어림
                    break;
                case "age_old":
                    orders.add(cb.asc(actor.get("birthYear"))); // 출생년도 낮을수록 나이 많음
                    break;
                case "height_tall":
                    orders.add(cb.desc(actor.get("height")));
                    break;
                case "height_short":
                    orders.add(cb.asc(actor.get("height")));
                    break;
                case "recent":
                default:
                    orders.add(cb.desc(actor.get("createdAt")));
                    break;
            }
        } else {
            // Pageable의 Sort 사용
            for (Sort.Order order : pageable.getSort()) {
                String property = order.getProperty();
                if (order.isDescending()) {
                    orders.add(cb.desc(actor.get(property)));
                } else {
                    orders.add(cb.asc(actor.get(property)));
                }
            }
        }

        // 기본 정렬: 최신순
        if (orders.isEmpty()) {
            orders.add(cb.desc(actor.get("createdAt")));
        }

        return orders;
    }

    private long countWithFilters(ActorSearchRequest request) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<ActorProfile> actor = countQuery.from(ActorProfile.class);

        List<Predicate> predicates = buildPredicates(cb, actor, request);
        
        countQuery.select(cb.count(actor));
        countQuery.where(predicates.toArray(new Predicate[0]));

        return entityManager.createQuery(countQuery).getSingleResult();
    }
}
